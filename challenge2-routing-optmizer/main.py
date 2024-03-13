from optimizer.base_functions import build_response
from optimizer.genetics import run_ga
from optimizer.pso import run_pso
from forecast import calculate_demand
from output_generator import save_json

targetWeek = "23-12-2018/29-12-2018"

population_size = 200
algorithm = "GA"   # this should be either "GA" or "PSO"
num_generations = 300

population_size = 1000
algorithm = "PSO"
num_generations = 100

work_dir = 'C:\\Projects\\MEIA1s-pprog-engcia\\'
json_file_output = work_dir + 'challenge2\\api\\output.json'
model_lib = work_dir + 'challenge2\\parte_aaut1\\results\\models\\file.joblib'
dataset_src = work_dir + 'challenge2\\parte_aaut1\\data\\raw\\superstore_sales_dataset.csv'

# work_dir = '/home/jd/Documents/ISEP_MEIA/00-SistemaPericial/MEIA1s-pprog-engcia/'
# json_file_output = work_dir + 'challenge2/api/output.json'
# model_lib = work_dir + 'challenge2/parte_aaut1/results/models/furniture_linear_regression.joblib'
# dataset_src = work_dir + 'challenge2/parte_aaut1/data/raw/superstore_sales_dataset.csv'

# TODO: Adjust city lists
demand = {
    "Los Angeles": calculate_demand(targetWeek, "Los Angeles", model_lib, dataset_src),
    "San Diego": calculate_demand(targetWeek, "San Diego", model_lib, dataset_src),
    "San Jose": calculate_demand(targetWeek, "San Jose", model_lib, dataset_src),
    "Fresno": calculate_demand(targetWeek, "Fresno", model_lib, dataset_src),
    "San Francisco": calculate_demand(targetWeek, "San Francisco", model_lib, dataset_src),
    "Sacramento": calculate_demand(targetWeek, "Sacramento", model_lib, dataset_src)
}

print("demand: ", demand)

map_cities = {
    'Sacramento': {'San Jose': 192, 'Anaheim': 656, 'Fresno': 270, 'Santa Ana': 665, 'San Francisco': 140, 'Los Angeles': 616, 'Oakland': 130, 'Long Beach': 651, 'San Diego': 806},
    'San Jose': {'Sacramento': 192, 'Anaheim': 584, 'Fresno': 238, 'Santa Ana': 593, 'San Francisco': 77, 'Los Angeles': 544, 'Oakland': 64, 'Long Beach': 579, 'San Diego': 734},
    'Anaheim': {'Sacramento': 656, 'San Jose': 584, 'Fresno': 392, 'Santa Ana': 11, 'San Francisco': 652, 'Los Angeles': 42, 'Oakland': 633, 'Long Beach': 41, 'San Diego': 152},
    'Fresno': {'Sacramento': 270, 'San Jose': 238, 'Anaheim': 392, 'Santa Ana': 400, 'San Francisco': 299, 'Los Angeles': 350, 'Oakland': 280, 'Long Beach': 385, 'San Diego': 540},
    'Santa Ana': {'Sacramento': 665, 'San Jose': 593, 'Anaheim': 11, 'Fresno': 400, 'San Francisco': 662, 'Los Angeles': 51, 'Oakland': 643, 'Long Beach': 45, 'San Diego': 142},
    'San Francisco': {'Sacramento': 140, 'San Jose': 77, 'Anaheim': 652, 'Fresno': 299, 'Santa Ana': 662, 'Los Angeles': 612, 'Oakland': 19, 'Long Beach': 648, 'San Diego': 803},
    'Los Angeles': {'Sacramento': 616, 'San Jose': 544, 'Anaheim': 42, 'Fresno': 350, 'Santa Ana': 51, 'San Francisco': 612, 'Oakland': 592, 'Long Beach': 38, 'San Diego': 192},
    'Oakland': {'Sacramento': 130, 'San Jose': 64, 'Anaheim': 633, 'Fresno': 280, 'Santa Ana': 643, 'San Francisco': 19, 'Los Angeles': 592, 'Long Beach': 633, 'San Diego': 788},
    'Long Beach': {'Sacramento': 651, 'San Jose': 579, 'Anaheim': 41, 'Fresno': 385, 'Santa Ana': 45, 'San Francisco': 648, 'Los Angeles': 38, 'Oakland': 633, 'San Diego': 179},
    'San Diego': {'Sacramento': 806, 'San Jose': 734, 'Anaheim': 152, 'Fresno': 540, 'Santa Ana': 142, 'San Francisco': 803, 'Los Angeles': 192, 'Oakland': 788, 'Long Beach': 179}
}

warehouses = {
    "Oakland": {"Technology": 8, "Office Supplies": 20, "Furniture": 10},
    "Long Beach": {"Technology": 26, "Office Supplies": 50, "Furniture": 4},
    "Anaheim": {"Technology": 6, "Office Supplies": 20, "Furniture": 4},
    "Santa Ana": {"Technology": 77, "Office Supplies": 80, "Furniture": 20},
}

vehicles = {
    "Vehicle1": {"Origin": "Oakland", "Capacity": 80, "Cost": 40, 'Prod_count': {"Technology": 0, "Office Supplies": 0, "Furniture": 0}},
    "Vehicle2": {"Origin": "Anaheim", "Capacity": 100, "Cost": 50, 'Prod_count': {"Technology": 0, "Office Supplies": 0, "Furniture": 0}},
}

match algorithm:
    case "PSO":
        best_route = run_pso(map_cities, vehicles, demand, warehouses, pop_size=population_size, max_iteration=num_generations)
    case "GA":
        best_route = run_ga(map_cities, vehicles, demand, warehouses, num_population=population_size, num_generations=num_generations)
    case default:
        raise NotImplementedError()

best_route_response = build_response(best_route, map_cities, vehicles, demand, warehouses)

save_json(best_route_response, warehouses, demand, vehicles,
          str.replace(json_file_output, ".json", "_" + algorithm.lower() + ".json"))
