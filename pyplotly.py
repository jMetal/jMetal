#!/usr/bin/env python3
"""Simple Pareto front plotter using Plotly."""

import sys
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go

if len(sys.argv) != 2:
    print("Usage: python pyplotly.py <file.csv>")
    sys.exit(1)

data = pd.read_csv(sys.argv[1], header=None)
n_obj = data.shape[1]

if n_obj == 2:
    fig = px.scatter(data, x=0, y=1, labels={'0': 'f1', '1': 'f2'}, title='Pareto Front')
elif n_obj == 3:
    fig = px.scatter_3d(data, x=0, y=1, z=2, labels={'0': 'f1', '1': 'f2', '2': 'f3'}, title='Pareto Front')
else:
    data.columns = [f'f{i+1}' for i in range(n_obj)]
    fig = px.parallel_coordinates(data, title='Pareto Front')

fig.show()
