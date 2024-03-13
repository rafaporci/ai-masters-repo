import operator
import numpy as np
import seaborn as sns
import random
import collections
import math
import time

from deap import base, creator, tools  # python -m pip install deap
from optimizer.base_functions import get_locations_array, create_individual, evaluate_individual

sns.set()


# The initialization consist in generating a random position and a random speed for a particle.
# The next function creates a particle and initializes its attributes,
# except for the attribute best, which will be set only after evaluation
def generate_particle(map_cities, vehicles, size, s_min, s_max):
    part = creator.Particle(create_individual(map_cities, vehicles))
    part.speed = [random.uniform(s_min, s_max) for _ in range(size)]
    part.smin = s_min
    part.smax = s_max
    return part


def create_particle(vals, s_min, s_max):
    part = creator.Particle(vals)
    part.speed = [random.uniform(s_min, s_max) for _ in range(len(vals))]
    part.smin = s_min
    part.smax = s_max
    return part


def update_particle(part, best, phi1, phi2, map_cities, vehicles):
    locations_number = len(get_locations_array(map_cities))
    start_index = 0
    for _ in vehicles:
        # in the particle, we have routes per vehicle, that's why we need to update
        # the positions considering the vehicle, it's ensure we continue generating
        # feasible solutions
        final_particle, updated_speed = update_partial_particle(
            part[start_index:start_index + locations_number],
            part.smin,
            part.smax,
            part.best[start_index:start_index + locations_number],
            part.speed[start_index:start_index + locations_number],
            best[start_index:start_index + locations_number],
            phi1,
            phi2)

        # ensure we respect the vehicle starting point
        ind_start = final_particle.index(part[start_index])
        if ind_start != 0:
            swap_positions(final_particle, 0, ind_start)
            swap_positions(updated_speed, 0, ind_start)

        part[start_index:start_index + locations_number] = final_particle
        part.speed[start_index:start_index + locations_number] = updated_speed


def swap_positions(array, pos_x, pos_y):
    tmp = array[pos_x]
    array[pos_x] = array[pos_y]
    array[pos_y] = tmp


def update_partial_particle(part, part_smin, part_smax, part_best, part_speed, best, phi1, phi2):
    u1 = (random.uniform(0, phi1) for _ in range(len(part)))
    u2 = (random.uniform(0, phi2) for _ in range(len(part)))

    # the particle's best position
    v_u1 = map(operator.mul, u1, map(operator.sub, part_best, part))

    # the neighbourhood best
    v_u2 = map(operator.mul, u2, map(operator.sub, best, part))

    # update particle speed
    part_speed = list(map(operator.add, part_speed, map(operator.add, v_u1, v_u2)))

    # speed limits
    for i, speed in enumerate(part_speed):
        if abs(speed) < part_smin:
            part_speed[i] = math.copysign(part_smin, speed)

        # adjust maximum speed if necessary
        elif abs(speed) > part_smax:
            part_speed[i] = math.copysign(part_smax, speed)

    new_part = list(map(operator.add, part, part_speed))
    return validate_particle(new_part), part_speed


def remove_duplicates(vals):
    duplic = [item for item, count in collections.Counter(vals).items() if count > 1]
    uniq_part = []
    offset = 0.001
    count = [1] * len(duplic)
    for val in vals:
        if val in duplic:
            ind = duplic.index(val)
            val += offset * count[ind]
            count[ind] += 1
        uniq_part.append(val)

    return uniq_part


# Change floats to integers and deal with duplicates
def validate_particle(particle):
    unique_part = remove_duplicates(particle)
    sorted_asc = sorted(unique_part, key=float)
    validated_part = []

    if len(sorted_asc) > len(set(sorted_asc)):
        print("problem")

    for val in unique_part:
        index = sorted_asc.index(val)
        validated_part.append((index + 1))

    # Check if some val exceeds the encoding limits (list of cities).
    # It can be caused by the particle updating + rounding,
    # ex.: 5+0.8 (max speed) = 5.8 it will be rounded to 6, but we just have 0-5 cities.
    # We can also have some cities being removed from the solution, ex.: 4+0.8 is rounded to 5.
    # if the city 4 is no longer part of solution (we will probably have the city 5 twice, but
    # it will be handled by the remove duplicates function).
    missing_parts = list(s for s in range(0, len(validated_part) - 1) if s not in validated_part)
    validated_part = list(
        missing_parts.pop() if val < 0 or val > len(validated_part) - 1 else val for val in validated_part)

    return validated_part


def run_pso(map_cities, vehicles, demand, warehouses,
            pop_size=200, max_iteration=1000,
            cognitive_coef=4, social_coef=2, s_limit=4):
    """
    runs the pso and prints the solution
    (source: https://deap.readthedocs.io/en/master/examples/pso_basic.html)
    Once the operators are registered in the toolbox, we fire up the algorithm by first creating a new population,
    and then applying the original PSO algorithm.
    The variable 'best' contains the best particle ever found (known as gbest in the original algorithm).
    """
    particle_size = len(vehicles) * len(map_cities)

    creator.create('Fitness_Func', base.Fitness, weights=(1.0, -0.7))
    creator.create("Particle", list, fitness=creator.Fitness_Func, speed=list,
                   smin=None, smax=None, best=None)

    toolbox = base.Toolbox()
    toolbox.register("particle", generate_particle, map_cities=map_cities, vehicles=vehicles, size=particle_size, s_min=-s_limit, s_max=s_limit)
    toolbox.register("population", tools.initRepeat, list, toolbox.particle)
    toolbox.register("update", update_particle, phi1=cognitive_coef, phi2=social_coef, map_cities=map_cities, vehicles=vehicles)
    toolbox.register('evaluate', evaluate_individual, map_cities, vehicles, demand, warehouses)

    pop = toolbox.population(n=pop_size)

    best = None
    iter_num = 0
    previous_best = None  # TODO: se formos inverter o custo, deve ser inicializado com 0

    print('### PSO - EVOLUTION START ###')
    start = time.time()

    for g in range(max_iteration):

        fit_count = 0
        for part in pop:
            part.fitness.values = toolbox.evaluate(part)
            if previous_best is None or part.fitness.values > previous_best:  # TODO: inverter se a formos maximizar
                previous_best = part.fitness.values
                iter_num = g + 1
            elif part.fitness.values == previous_best:
                fit_count += 1

        if fit_count > int(np.ceil(pop_size * 0.15)):
            rand_pop = toolbox.population(n=pop_size)
            for part in rand_pop:
                part.fitness.values = toolbox.evaluate(part)
            some_inds = tools.selRandom(rand_pop, int(np.ceil(pop_size * 0.1)))  # random pop here
            mod_pop = tools.selWorst(pop, int(np.ceil(pop_size * 0.9)))
        else:
            some_inds = tools.selBest(pop, int(np.ceil(pop_size * 0.05)))  # elite pop here
            mod_pop = tools.selRandom(pop, int(np.ceil(pop_size * 0.95)))

        mod_pop = list(map(toolbox.clone, mod_pop))

        for part in mod_pop:
            if not part.best or part.best.fitness < part.fitness:  # TODO: inverter se a formos maximizar
                part.best = creator.Particle(part)
                part.best.fitness.values = part.fitness.values
            if not best or best.fitness < part.fitness:  # TODO: inverter se a formos maximizar
                best = creator.Particle(part)
                best.fitness.values = part.fitness.values

        for part in mod_pop:
            toolbox.update(part, best)

        mod_pop.extend(some_inds)
        pop[:] = mod_pop

        # Gather all the stats in one list and print them
        print(f'Generation: {g} | Best Fulfillment %: {best.fitness.values[0]:.2f} | Best Cost: {best.fitness.values[1]:.2f}')

    end = time.time()
    print('### EVOLUTION END ###')
    best_ind = best
    print(f'Best individual: {best_ind}')

    print(f'% Fulfillment: {round(best_ind.fitness.values[0], 2)}')
    print(f'Total cost: {round(best_ind.fitness.values[1], 2)}')
    print(f'Found in (iteration): {iter_num}')
    print(f'Execution time (s): {round(end - start, 2)}')

    return best_ind
