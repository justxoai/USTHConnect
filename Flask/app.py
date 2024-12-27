from flask import Flask, jsonify

import pandas as pd

from kmodes.kmodes import KModes # K-Modes
import joblib # Save and load model
from Preprocessing import * # Preprocess data
from K_Elbow import * # K-Elbow method
from sklearn.metrics import silhouette_score, davies_bouldin_score # Evaluation metrics

app = Flask(__name__)

# Path to the dataset
path = r"D:\\Justxoai\\Code\\Python\\Draft\\dataset.csv"

# Function
# Training Model
def training_model(df, save_file="kmodes_model1.pkl", eval_metrics=False, init="Huang", n_init=15, random_state=42):
    # Preprocess data
    train_data = data_preprocess(df)
    
    #K Elbow choosing
    md = KModes(init=init, n_init=n_init, random_state=random_state)
    best_k = elbow_method(train_data, md)
    print(f"Number of cluster: {best_k}")
    
    # Training model
    km = KModes(n_clusters=best_k, init=init, n_init=n_init, random_state=random_state)
    km.fit(train_data)

    # Evaluation metrics
    if eval_metrics:
        clusters = km.predict(train_data)
        print(f"Silhouette Score (Hamming): {silhouette_score(train_data, clusters, metric="hamming")}")
        print(f"Davies Bouldin Score: {davies_bouldin_score(train_data, clusters)}")
        
    # Save the K-Modes model
    model_filename = save_file
    joblib.dump(km, model_filename)
    print(f"Model saved as {model_filename}")

    # Return the model
    return km

# Assign Cluster
def assign_cluster(model, df):
    train_data = data_preprocess(df)
    pred = model.predict(train_data)

    df["Cluster"] = pred
    return df

# Recommend List
def recommend_cluster(model, data, df):
    train_data = transfer_data(data)  # Preprocess data for prediction
    pred = model.predict(train_data)  # Predict the cluster

    # Collect recommendations
    recommendations = []
    for idx, row in df[df["Cluster"] == pred[0]].iterrows():
        if str(row["FullName"]) != str(data["FullName"]):  # Exclude the input student
            recommendations.append(row["FullName"])

    return jsonify(recommendations)
    # return recommendations

@app.route("/")
def home():
    return "StudyBuddy Recommendation System for USTHConnect!"

@app.route("/train", methods=["GET"])
def train_model():
    # Set global variable
    global model
    global df

    # Load and preprocess the dataset
    df = pd.read_csv(path)

    # Train the model
    model = training_model(df, save_file="kmodes_model2.pkl")

    # Assign clusters
    df = assign_cluster(model, df)

    return "Train complete"

@app.route("/train/sample/<string:name>/<string:gender>/<string:major>/<string:interest>/<string:communication>/<string:looking_for>/<string:fav_subject>/<string:location>/<string:time>/<string:personality>")
def load_sample(name,gender,major,interest,communication,looking_for,fav_subject,location,time,personality):
    # Set global variable
    global sample_data

    # Load data for prediction
    sample_data = pd.DataFrame({
        "FullName": [name],
        "Gender": [gender],
        "Major": [major],
        "Interests": [interest],
        "Communication_Style": [communication],
        "Looking_for": [looking_for],
        "Favorite_Subject": [fav_subject],
        "Study_Location": [location],
        "Study_Time": [time],
        "Personality": [personality]
    })

    return "Load Sample Complete!"

@app.route("/train/sample/<string:name>/<string:gender>/<string:major>/<string:interest>/<string:communication>/<string:looking_for>/<string:fav_subject>/<string:location>/<string:time>/<string:personality>/recommend")
def recommend_model(name,gender,major,interest,communication,looking_for,fav_subject,location,time,personality):
    # Return the results as a JSON response
    recommendations = recommend_cluster(model, sample_data, df)

    return recommendations


if __name__ == "__main__":
    app.run(debug=True)
