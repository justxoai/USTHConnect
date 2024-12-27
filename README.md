### USTHConnect Recommend System Model using Flask ###

### Flask:
-  Model Function:
+ trainning_model: Training Model
+ assign_cluster: Assign Cluster
+ recommend_cluster: Return Recommend Study Buddy in file JSON

- API Function:
+ train_model: /train 
=> training model before load data and recommend
+ load_sample: /train/sample/.....
=> load data to predict
+ recommend_model: /train/sample/.../recommend
=> return json file of recommend list

### Recommend Model:
- K_Elbow.py
- Preprocessing

### Dataset:
- dataset.csv