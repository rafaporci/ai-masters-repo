from typing import Optional, List
from datetime import datetime


class Product:
    id: str
    quantity: int

    def __init__(self, _id: str, quantity: int) -> None:
        self.id = _id
        self.quantity = quantity


class Demand:
    id: Optional[str]
    products: List[Product]
    name: Optional[str]

    def __init__(self, _id: Optional[str], products: List[Product], name: Optional[str]) -> None:
        self.id = _id
        self.products = products
        self.name = name


class Payload:
    product_id: str
    quantity: int

    def __init__(self, product_id: str, quantity: int) -> None:
        self.product_id = product_id
        self.quantity = quantity


class Point:
    id: str
    payload: List[Payload]

    def __init__(self, _id: str, payload: List[Payload]) -> None:
        self.id = _id
        self.payload = payload


class Route:
    point: Point

    def __init__(self, point: Point) -> None:
        self.point = point


class Vehicle:
    id: str
    warehouse_origin_id: str
    cost_per_km: int
    capacity: int
    route: List[Route]

    def __init__(self, _id: str, warehouse_origin_id: str, cost_per_km: int, capacity: int, route: List[Route]) -> None:
        self.id = _id
        self.warehouse_origin_id = warehouse_origin_id
        self.cost_per_km = cost_per_km
        self.capacity = capacity
        self.route = route


class StockLevel:
    id: str
    level: int

    def __init__(self, _id: str, level: int) -> None:
        self.id = _id
        self.level = level


class Warehouse:
    id: str
    stock_levels: List[StockLevel]

    def __init__(self, _id: str, stock_levels: List[StockLevel]) -> None:
        self.id = _id
        self.stock_levels = stock_levels


class Result:
    run_time_date: datetime
    warehouses: List[Warehouse]
    demand: List[Demand]
    vehicles: List[Vehicle]

    def __init__(self, run_time_date: datetime, warehouses: List[Warehouse], demand: List[Demand], vehicles: List[Vehicle]) -> None:
        self.run_time_date = run_time_date
        self.warehouses = warehouses
        self.demand = demand
        self.vehicles = vehicles
