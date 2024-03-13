import joblib
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns; sns.set()
import pandas as pd 
import os
from datetime import datetime
import re
import warnings

warnings.filterwarnings("ignore")

TipoProdutos = ["Technology", "Office Supplies", "Furniture"]

quantity_columns = ['NumberOfItemsSold', 'QuantityOnWeekMinus1', 'QuantityOnWeekMinus2', 'QuantityOnWeekMinus3']
tendency_columns = ['TendencyOnWeekMinus1', 'TendencyOnWeekMinus2', 'TendencyOnWeekMinus3']
season_columns = ['SalesSeason', 'Season_1', 'Season_2']
rollin_mean_column = ['RollingMean3Weeks']

current_directory = os.path.dirname(os.path.abspath(__file__))
current_directory = os.path.abspath(os.path.join(current_directory, os.pardir, os.pardir))

season_mapping = {
    **dict.fromkeys(range(1, 12), "00"),
    **dict.fromkeys(range(12, 25), "01"),
    **dict.fromkeys(range(25, 38), "10"),
    **dict.fromkeys(range(38, 51), "11"),
    **dict.fromkeys(range(51, 54), "00")
}


def calculate_demand(semana, estado, model_path, dataset_path):
    model_path = os.path.join(current_directory, model_path)
    dataset_path = os.path.join(current_directory, dataset_path)

    df = pd.read_csv(dataset_path)

    # Remove unecessary columns
    df = df.drop("Order ID", axis=1)
    df = df.drop("Ship Date", axis=1)
    df = df.drop("Ship Mode", axis=1)
    df = df.drop("Customer ID", axis=1)
    df = df.drop("Customer Name", axis=1)
    df = df.drop("Segment", axis=1)
    df = df.drop("Country", axis=1)
    df = df.drop("Postal Code", axis=1)
    df = df.drop("Region", axis=1)
    df = df.drop("Product ID", axis=1)
    df = df.drop("Sub-Category", axis=1)
    df = df.drop("Product Name", axis=1)
    df = df.drop("City", axis=1)
    df = df.drop("Sales", axis=1)

    # Convert Order Date to datetime
    df['Order Date'] = pd.to_datetime(df['Order Date'], format='%d/%m/%Y')

    date_split = semana.split("/")

    # start_date need to be exactly 3 weeks before.
    start_date = pd.to_datetime(date_split[0], format='%d-%m-%Y') - pd.Timedelta(days=21)

    filtered_df = df[(df['Order Date'] >= start_date) & (df['Order Date'] <= date_split[1])]
    filtered_df = filtered_df.sort_values(by='Order Date')

    # Change the dates to a number of the week
    filtered_df['WeekNumber'] = (filtered_df['Order Date'] - filtered_df['Order Date'].min()).dt.days // 7 + 1

    # Drop unecessary column
    filtered_df = filtered_df.drop("Order Date", axis=1)

    # Filter by state
    filtered_df = filtered_df[filtered_df['State'] == estado]

    # Empty array
    array_pred = []

    for produto in TipoProdutos:
        # Only for 1 product at a time
        filtered_df2 = filtered_df[filtered_df['Category'] == produto]

        # Group by Week --
        # Array with all possible weeks (1-4)
        all_weeks = pd.DataFrame({'WeekNumber': range(1, 5)})

        weekly_sales = filtered_df2.groupby('WeekNumber').size().reset_index(name='NumberOfItemsSold')

        # Perform a left join to include weeks with no sales
        weekly_df = all_weeks.merge(weekly_sales, on='WeekNumber', how='left').fillna(0)

        # Quantity sold previous week
        weekly_df['QuantityOnWeekMinus1'] = weekly_df['NumberOfItemsSold'].shift(1)
        weekly_df['QuantityOnWeekMinus1'].fillna(0, inplace=True)

        # Quantity sold two weeks before
        weekly_df['QuantityOnWeekMinus2'] = weekly_df['NumberOfItemsSold'].shift(2)
        weekly_df['QuantityOnWeekMinus2'].fillna(0, inplace=True)

        # Quantity sold three weeks before (Temporary)
        weekly_df['QuantityOnWeekMinus3'] = weekly_df['NumberOfItemsSold'].shift(3)
        weekly_df['QuantityOnWeekMinus3'].fillna(0, inplace=True)

        weekly_df['TendencyOnWeekMinus1'] = (weekly_df['QuantityOnWeekMinus1'] - weekly_df['QuantityOnWeekMinus2']).apply(lambda x: 1 if x > 0 else 0)
        weekly_df['TendencyOnWeekMinus2'] = (weekly_df['QuantityOnWeekMinus2'] - weekly_df['QuantityOnWeekMinus3']).apply(lambda x: 1 if x > 0 else 0)

        weekly_df['QuantityOnWeekMinus4'] = weekly_df['NumberOfItemsSold'].shift(4)
        weekly_df['QuantityOnWeekMinus4'].fillna(0, inplace=True)

        weekly_df['TendencyOnWeekMinus3'] = (weekly_df['QuantityOnWeekMinus3'] - weekly_df['QuantityOnWeekMinus4']).apply(lambda x: 1 if x > 0 else 0)

        weekly_df.drop(columns=['QuantityOnWeekMinus4'], inplace=True)

        weekly_df['RollingMean3Weeks'] = weekly_df['NumberOfItemsSold'].shift(1).rolling(window=3).mean()
        weekly_df['RollingMean3Weeks'].fillna(0, inplace=True)

        # Only need week 4
        week_4_df = weekly_df[weekly_df['WeekNumber'] == 4]

        # Change the number 4 for the correct week number
        initial_date = datetime.strptime(date_split[0], "%d-%m-%Y")
        week_number = initial_date.strftime("%U")
        week_4_df.loc[week_4_df.index, 'WeekNumber'] = week_number

        # Certify WeekNumber is int
        week_4_df['WeekNumber'] = week_4_df['WeekNumber'].astype(int)

        # Add the SalesSeasons
        week_4_df['SalesSeason'] = np.where(((week_4_df['WeekNumber'] >= 51) & (week_4_df['WeekNumber'] <= 53)) |
                                            ((week_4_df['WeekNumber'] >= 46) & (week_4_df['WeekNumber'] <= 48)) |
                                            (week_4_df['WeekNumber'] == 1), 1, 0)

        # Add the Seasons
        week_4_df['Season'] = week_4_df['WeekNumber'].map(season_mapping)
        # Extract the two binary columns for seasons
        week_4_df['Season_1'] = week_4_df['Season'].apply(lambda x: int(x[0]))
        week_4_df['Season_2'] = week_4_df['Season'].apply(lambda x: int(x[1]))
        # Drop the original 'Season' column
        week_4_df = week_4_df.drop('Season', axis=1)

        # Organize columns so that quantity and tendency columns stay together
        new_order = ['WeekNumber'] + quantity_columns + tendency_columns + season_columns + rollin_mean_column
        week_4_df = week_4_df[new_order]

        model_name = produto.replace(" ", "").lower()
        new_model_path = re.sub(r'\\([^\\]+)\.joblib$', f'\\\\{model_name}_linear_regression.joblib', model_path)
        model = joblib.load(new_model_path)

        # Make prediction
        target_columns = ['NumberOfItemsSold']
        X = week_4_df.drop(target_columns, axis=1)
        y = week_4_df['NumberOfItemsSold']

        y_pred = model.predict(X)
        array_pred.append(y_pred[0][0])

    return {"Technology": round(array_pred[0]),
            "Office Supplies": round(array_pred[1]),
            "Furniture": round(array_pred[2])}
