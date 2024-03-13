import numpy as np
import random

from optimizer.calc_stock_movements import calculate_stock_movements


def create_individual(_map, vehicles):
    """ Generates initial solutions
    route1 = [ 1, 4, 2, 3 ]
    route2 = [ 4, 2, 1, 2 ]
    final list = [ 1, 4, 2, 3, 4, 2, 1, 2 ] """
    vehicle_routes = {}
    for vehicle in vehicles:
        route = []
        for destination in _map[vehicles[vehicle]["Origin"]]:
            route.append(destination)

        route = random_sort(route)

        route.insert(0, vehicles[vehicle]["Origin"])

        vehicle_routes[vehicle] = route

    return list(
        np.array(list(transcode_to_chromo(vehicle_routes[vehicle], _map) for vehicle in vehicle_routes.keys())).flat)


def transcode_to_chromo(route, _map):
    return list(get_location_by_id(location, _map) for location in route)


def transcode_from_chromo(route, _map):
    return list(get_location_by_index(location, _map) for location in route)


def get_location_by_id(location, _map):
    return get_locations_array(_map).index(location)


def get_location_by_index(index, _map):
    return get_locations_array(_map)[index]


def get_locations_array(_map):
    locations = set()
    for origins, connections in _map.items():
        locations.add(origins)
        locations.update(conn for conn in connections)
    locations = list(locations)
    locations.sort()
    return locations


def get_location_index(_map):
    locations = set()
    for origins, connections in _map.items():
        locations.add(origins)
        locations.update(conn for conn in connections)
    locations = list(locations)
    locations.sort()
    return locations


def random_sort(input_list):
    shuffled_list = input_list.copy()
    random.shuffle(shuffled_list)
    return shuffled_list


def calc_distance(_map, origin, destination):
    return _map[origin][destination]


def evaluate_individual(map_cities, vehicles, demand, warehouses, chromo):
    chromo_t = transcode_from_chromo(chromo, map_cities)

    perc_demand, cs_stock_movements = calc_stock_movements(chromo_t, demand, warehouses, vehicles)
    cost = calculate_cost(map_cities, vehicles, cs_stock_movements)
    return perc_demand, cost


# here chromo_t will be the cs_movements (and with repeated cities) in string format.
def calculate_cost(map_cities, vehicles, cs_movements):
    cost = 0
    for vehicle in cs_movements['Vehicles']:
        last_location = ""
        for location in cs_movements['Vehicles'][vehicle]:
            if last_location != "":
                cost = cost + get_distance(map_cities,
                                           last_location,
                                           location) * vehicles[vehicle]["Cost"]

            last_location = location
    return cost


def get_distance(map_cities, origin, destination):
    return map_cities[origin][destination]


def calc_stock_movements(chromo, demand, warehouses, vehicles):
    route = chromo
    cs_movements = calculate_stock_movements(vehicles, warehouses, demand, route)
    cs_movements = remove_zero_stock_movements(cs_movements, vehicles)
    fulfillment = calculte_fulfillment_per_product(cs_movements, demand)

    quantity_fulfillment = summarize(fulfillment)
    quantity_demand = summarize(demand)

    percent = quantity_fulfillment / quantity_demand
    return percent, cs_movements


def summarize(product_by_location):
    quantity = 0
    for location in product_by_location:
        for product in product_by_location[location]:
            quantity = quantity + product_by_location[location][product]
    return quantity


def remove_zero_stock_movements(cs_movements, vehicles):
    locations_to_remove = []
    for vehicle in cs_movements['Vehicles']:
        for location in cs_movements['Vehicles'][vehicle]:
            if location != vehicles[vehicle]['Origin']:
                quantity = 0
                for product in cs_movements['Vehicles'][vehicle][location]:
                    quantity = quantity + cs_movements['Vehicles'][vehicle][location][product]
                if quantity == 0:
                    locations_to_remove.append([vehicle, location])

    for locationToRemove in locations_to_remove:
        cs_movements['Vehicles'][locationToRemove[0]].pop(locationToRemove[1])
    return cs_movements


def calculte_fulfillment_per_product(cs_movements, demand):
    fulfillment = {}

    for vehicle in cs_movements['Vehicles']:
        for location in cs_movements['Vehicles'][vehicle]:
            if location in demand:
                for product in cs_movements['Vehicles'][vehicle][location]:
                    if location not in fulfillment:
                        fulfillment[location] = {}
                    if product not in fulfillment[location]:
                        fulfillment[location][product] = 0

                    fulfillment[location][product] = fulfillment[location][product] + abs(
                        cs_movements['Vehicles'][vehicle][location][product])
    return fulfillment


def is_final_destination(location, demand):
    return location in demand.keys()


def build_response(chromo, _map, vehicles, demand, warehouses):
    chromo_t = transcode_from_chromo(chromo, _map)
    response = calc_stock_movements(chromo_t, demand, warehouses, vehicles)
    return response
