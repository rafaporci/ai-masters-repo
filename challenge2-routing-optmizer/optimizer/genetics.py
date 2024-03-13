import numpy as np
import seaborn as sns
import random
import copy
from deap import base, creator, tools  # python -m pip install deap
from optimizer.base_functions import get_locations_array, create_individual, evaluate_individual

sns.set()


def chromo_create(_map, vehicles):
    return [create_individual(_map, vehicles)]  # needed to extend the object (further ahead)


def chromo_eval(_map, vehicles, demand, warehouse, chromo):
    return evaluate_individual(_map, vehicles, demand, warehouse, chromo[0])


def crossover(_map, vehicles, chromo1, chromo2):
    start_index = 0
    locations_number = len(get_locations_array(_map))
    chromo_d1 = []
    chromo_d2 = []

    for _ in vehicles:
        chromo_d1.append(chromo1[0][start_index])
        chromo_d2.append(chromo2[0][start_index])
        p1, p2 = uniform_based_order_crossover(chromo1[0][start_index + 1:start_index + locations_number],
                                               chromo2[0][start_index + 1:start_index + locations_number])
        chromo_d1.extend(p1)
        chromo_d2.extend(p2)
        start_index = start_index + locations_number

    chromo1[0], chromo2[0] = chromo_d1, chromo_d2


def mutation(_map, mutation_rate, vehicles, chromo):
    start_index = 0
    locations_number = len(get_locations_array(_map))
    chromo_d = []

    for _ in vehicles:
        chromo_d.append(chromo[0][start_index])
        chromo_d.extend(simple_mutation(chromo[0][start_index + 1:start_index + locations_number], mutation_rate))
        start_index = start_index + locations_number

    chromo[0] = chromo_d


def simple_mutation(chromosome, mutation_rate):
    mutated_chromosome = chromosome.copy()

    for i in range(len(mutated_chromosome)):
        if random.uniform(0, 1) < mutation_rate:
            # Troca o gene atual com um gene aleatório
            j = random.randint(0, len(mutated_chromosome) - 1)
            mutated_chromosome[i], mutated_chromosome[j] = mutated_chromosome[j], mutated_chromosome[i]

    return mutated_chromosome


def uniform_based_order_crossover(individual_1, individual_2, shuffle_size=5):
    """
    uniform based order crossover
    :return: two children
    """

    child_1 = copy.deepcopy(individual_1)
    child_2 = copy.deepcopy(individual_2)

    size = min(len(child_1), len(child_2))
    if size < shuffle_size:
        shuffle_size = size - 1

    index = [*range(size)]
    bit_mask = set(random.sample(index, shuffle_size))

    shuffle_1 = [individual_1[idx] for idx in bit_mask]
    shuffle_2 = [individual_2[idx] for idx in bit_mask]

    inv_bit_mask = set(index) - bit_mask

    parent1 = [individual_1[i] for i in inv_bit_mask]
    parent2 = [individual_2[i] for i in inv_bit_mask]

    order_shuffle_1 = set(shuffle_1)
    order_shuffle_2 = set(shuffle_2)

    can_shuffle = order_shuffle_1 & order_shuffle_2
    remain_shuffle = order_shuffle_1 ^ order_shuffle_2

    # create order from data
    order_shuffle_1 = [x for x in shuffle_1 if x in can_shuffle]
    order_shuffle_2 = [x for x in shuffle_2 if x in can_shuffle]

    parent1 = [x for x in parent1 if x in remain_shuffle]
    parent2 = [x for x in parent2 if x in remain_shuffle]

    order_shuffle_1 += parent1
    order_shuffle_2 += parent2

    i = 0
    for idx in bit_mask:
        child_1[idx] = order_shuffle_2[i]
        child_2[idx] = order_shuffle_1[i]
        i += 1

    return child_1, child_2


def feasibility(_chromo):
    # All generated solution are viable (in theory)
    return


def run_ga(map_cities, vehicles, demand, warehouses, num_population=200, num_generations=1000, prob_crossover=.4, prob_mutation=.6):
    tb = base.Toolbox()

    creator.create('Fitness_Func', base.Fitness, weights=(1.0, -0.7))
    creator.create('Individual', list, fitness=creator.Fitness_Func)

    # function registration and GA loop execution
    tb.register('indexes', chromo_create, map_cities, vehicles)
    tb.register('individual', tools.initIterate, creator.Individual, tb.indexes)
    tb.register('population', tools.initRepeat, list, tb.individual)
    tb.register('evaluate', chromo_eval, map_cities, vehicles, demand, warehouses)
    tb.register('select', tools.selTournament)
    tb.register('mate', crossover, map_cities, vehicles)
    tb.register('mutate', mutation, map_cities, 0.2)
    tb.register('feasibility', feasibility)

    population = tb.population(n=num_population)

    fitness_set = list(tb.map(tb.evaluate, population))
    for ind, fit in zip(population, fitness_set):
        ind.fitness.values = fit

    best_fit_list = []
    best_sol_list = []

    best_fit = None

    print('### GA - EVOLUTION START ###')

    for gen in range(0, num_generations):
        if gen % 50 == 0 and best_fit is not None:
            print(f'Generation: {gen:4} | Best Fulfillment %: {best_fit[0]:.2f} | Best Cost: {best_fit[1]:.2f}' )

        offspring = tb.select(population, len(population), tournsize=3)
        offspring = list(map(tb.clone, offspring))

        for child1, child2 in zip(offspring[0::2], offspring[1::2]):
            if np.random.random() < prob_crossover:
                tb.mate(child1, child2)
                del child1.fitness.values
                del child2.fitness.values

        for chromo in offspring:
            if np.random.random() < prob_mutation:
                tb.mutate(vehicles, chromo)
                del chromo.fitness.values

        for chromo in offspring:
            tb.feasibility(chromo)

        invalid_ind = [ind for ind in offspring if not ind.fitness.valid]
        fitness_set = map(tb.evaluate, invalid_ind)
        for ind, fit in zip(invalid_ind, fitness_set):
            ind.fitness.values = fit

        population[:] = offspring

        curr_best_sol = tools.selBest(population, 1)[0]
        curr_best_fit = curr_best_sol.fitness.values

        if best_fit is None or curr_best_fit < best_fit:
            best_sol = curr_best_sol
            best_fit = curr_best_fit

        best_fit_list.append(best_fit)
        best_sol_list.append(best_sol)

    print('### EVOLUTION END ###')
    print(f'Best individual: {best_sol}')

    print(f'% Fulfillment: {round(best_sol.fitness.values[0], 2)}')
    print(f'Total cost: {round(best_sol.fitness.values[1], 2)}')

    return best_sol[0]
