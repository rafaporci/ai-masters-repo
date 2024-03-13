from datetime import datetime
import json


def save_json(best_routes, warehouses, demand, vehicles, output_path):
    # Convert to lists
    warehouses_list = [
        {"id": key, "stockLevels": [{"id": product, "level": level} for product, level in value.items()]}
        for key, value in warehouses.items()
    ]

    demand_list = [
        {"id": entry, "products": [{"id": product, "Quantity": demand[entry][product]} for product in demand[entry]]}
        for entry in demand
    ]

    vehicles_list = []
    for vehicle_id, vehicle_data in vehicles.items():
        vehicle_dict = {
            "id": vehicle_id,
            "warehouseOriginId": vehicle_data["Origin"],
            "costPerKm": vehicle_data["Cost"],
            "capacity": vehicle_data["Capacity"],
            "route": generate_routes_per_vehicle(vehicle_id, best_routes)
        }
        vehicles_list.append(vehicle_dict)

    # Get current UTC time
    current_time_utc = datetime.now()

    # datetime format
    formatted_time = current_time_utc.strftime("%Y-%m-%dT%H:%M:%SZ")

    # Write results to JSON
    output_data = {
        "runTimeDate": formatted_time,
        "warehouses": warehouses_list,
        "demand": demand_list,
        "vehicles": vehicles_list,
    }

    with open(output_path, 'w') as output_file:
        json.dump(output_data, output_file, indent=2)

    print(f"Resultados guardados em: {output_path}")


def generate_routes_per_vehicle(vehicle_id, best_routes):
    route = []
    for location in best_routes[1]['Vehicles'][vehicle_id]:
        route.append(
            {
                "point": {"id": location},
                "products": [
                    {"id": product, "quantity": best_routes[1]['Vehicles'][vehicle_id][location][product]} for product
                    in best_routes[1]['Vehicles'][vehicle_id][location]
                ]
            }
        )
    return route
