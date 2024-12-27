from kmodes.kmodes import KModes # K-Modes
import joblib # Save and load model
from Preprocessing import * # Preprocess data
from K_Elbow import * # K-Elbow method
from sklearn.metrics import silhouette_score, davies_bouldin_score # Evaluation metrics


# Some problems occur in this function, currently just save and load model
def training_model(df, save_file="kmodes_model1.pkl", eval_metrics=False, init='Huang', n_init=15, random_state=42):
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
        print(f"Silhouette Score (Hamming): {silhouette_score(train_data, clusters, metric='hamming')}")
        print(f"Davies Bouldin Score: {davies_bouldin_score(train_data, clusters)}")
        
    # Save the K-Modes model
    model_filename = save_file
    joblib.dump(km, model_filename)
    print(f"Model saved as {model_filename}")

    # Return the model
    return km
    
def assign_cluster(model, df):
    train_data = data_preprocess(df)
    pred = model.predict(train_data)

    df["Cluster"] = pred
    return df

def recommend_cluster(model, data, df):
    """
    Args:
        model
        data: data of student to recommend (not preprocessing)
        df: dataframe after processing with cluster column
    """
    train_data = transfer_data(data)
    pred = model.predict(train_data)
    
    print(f"Recommendation for {data.iloc[0].FullName}:")
    stu_idx = 1
    for idx, row in df[df["Cluster"] == pred[0]].iterrows():
        if str(row["FullName"]) != str(data["FullName"]): # Change to StudentID if needed
            print(f"{stu_idx}. {row['FullName']}")
            stu_idx += 1