import copy


def calculate_stock_movements(original_trucks, original_warehouses, original_shops, route_all_trucks):
    trucks = copy.deepcopy(original_trucks)
    warehouses = copy.deepcopy(original_warehouses)
    shops = copy.deepcopy(original_shops)
    shops_dynamic = copy.deepcopy(original_shops)

    all_stock_movements = {
        'Vehicles': {}
    }

    start_index = 0
    locations_count = len(original_shops) + len(original_warehouses)

    for truck_name, t_info in trucks.items():
        route = route_all_trucks[start_index:start_index + locations_count]

        if truck_name not in all_stock_movements['Vehicles']:
            all_stock_movements['Vehicles'][truck_name] = {}

        for i, current_city in enumerate(route):
            if current_city in warehouses:
                warehouse_stock_mov = handle_warehouse(i, route, shops_dynamic, warehouses, t_info)
                all_stock_movements['Vehicles'][truck_name].update(warehouse_stock_mov)
            elif current_city in shops:
                shops_stock_mov = handle_shop(shops[current_city], current_city, t_info)
                all_stock_movements['Vehicles'][truck_name].update(shops_stock_mov)
            else:
                raise ValueError(f'Unexpected. Current city ({current_city}) has no warehouse nor shop')

        start_index = start_index + locations_count
    return all_stock_movements


def handle_shop(current_city_shop, city_name, truck_info):
    """
    current_city_shop: is a dict
    """
    stock_movements = dict()
    if city_name not in stock_movements:
        stock_movements[city_name] = {}

    for prod, s_demand in current_city_shop.items():
        quantity_unloaded = min(s_demand, truck_info['Prod_count'][prod])
        if prod not in stock_movements[city_name]:
            stock_movements[city_name][prod] = 0 if prod == 0 else - quantity_unloaded  # the minus sign is important
            truck_info['Prod_count'][prod] -= quantity_unloaded
            current_city_shop[prod] -= quantity_unloaded
        else:
            if prod > 0:
                stock_movements[city_name][prod] -= quantity_unloaded  # the minus sign is important
                truck_info['Prod_count'][prod] -= quantity_unloaded
    return stock_movements


def handle_warehouse(route_idx, route, _shops_dynamic, warehouses, truck_info):
    """
    :param: route has the format [C.BRAGA, C.PORTO, ...]
    """
    stock_movements = dict()
    current_city = route[route_idx]

    next_shops_dem = get_next_shops_demand(route_idx, route, _shops_dynamic)

    for dict_demand in next_shops_dem:
        for store_name, st_demands in dict_demand.items():

            for w_prod, w_prod_qnt in warehouses[current_city].items():
                for s_prod, s_prod_dem in st_demands.items():
                    if w_prod != s_prod:
                        continue
                    if current_city not in stock_movements:
                        stock_movements[current_city] = {}

                    truck_free_space = get_truck_free_space(truck_info['Capacity'], truck_info['Prod_count'])
                    quantity_loaded = min(truck_free_space, s_prod_dem, warehouses[current_city][w_prod])

                    warehouses[current_city][w_prod] -= quantity_loaded
                    _shops_dynamic[store_name][w_prod] -= quantity_loaded  # update demand to avoid reload in next step

                    truck_info['Prod_count'][
                        w_prod] += quantity_loaded  # truck capacity is immutable.

                    if w_prod not in stock_movements[current_city]:
                        stock_movements[current_city][w_prod] = quantity_loaded
                    else:
                        stock_movements[current_city][w_prod] += quantity_loaded
    return stock_movements


def get_next_shops_demand(_idx, _route, _shops):
    """
    :returns: the next shop demands as a list of dicts which is for ex:
     [{'PORTO': {'Produto1': 500, 'Produto2': 500}},
     {'VIANA': {'Produto1': 20, 'Produto2': 5500}}]

     :param: _route example: [C.BRAGA, C.PORTO, C.PAREDES, C.GAIA, C.AVEIRO, C.VIANA]
    """
    all_nsd = list()
    nsd = dict()
    for j in range(_idx, len(_route) - 1):
        next_city = _route[j + 1]
        if next_city not in _shops:
            continue

        if next_city not in nsd:
            nsd = {next_city: {}}

        nsd[next_city] = _shops[next_city]
        all_nsd.append(nsd)
    return all_nsd


def get_truck_free_space(_t_capacity, _t_prod_count):
    """
    capacity is immutable and restricts the quantity of stored products in the truck.
    :param_ _t_prod_count is a dict with format ex: {'Produto1': 0, 'Produto2': 0}
    """
    load = 0
    for prod, prod_count in _t_prod_count.items():
        load += prod_count

    if _t_capacity < load:
        raise ValueError(f'Truck capacity ({_t_capacity}) can never be less than truck load ({load})')
    return _t_capacity - load
