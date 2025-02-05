from sklearn.cluster import KMeans
import sizePattern
import numpy as np

np.random.seed(0)
from sklearn.ensemble import RandomForestRegressor

def train_random_forest(X, y):
    model = RandomForestRegressor(n_estimators=100, random_state=42)  
    model.fit(X, y)
    return model


def active_learning(data,n_sortedCluster,nbudget):
    center_vec_sorted = n_sortedCluster
    top_indice= [item[0] for item in center_vec_sorted[:nbudget]]
    sampled_data_ids=[]
    sampled_data=[]
    for indice in top_indice:
        sampled_data_ids.append(indice)
        sampled_data.append(data[indice])
    all_data_ids=set(range(len(data)))
    test_data_ids = list(all_data_ids - set(sampled_data_ids))
    return sampled_data_ids,sampled_data,test_data_ids


def human_annotation_stim(data, n_clusters):
    kmeans = KMeans(n_clusters=n_clusters,random_state=42)
    kmeans.fit(data)
    labels = kmeans.labels_
    centers = kmeans.cluster_centers_

    sorted_dict = {}
    for unique_val in sorted(set(labels)):
        indices = np.where(labels == unique_val)[0]
        distances = np.linalg.norm(data[indices] - centers[unique_val], axis=1)
        sorted_indices = indices[np.argsort(distances)]
        sorted_dict[unique_val] = sorted_indices.tolist()

    real_labels = {}
    favorite = 1
    for i in range(len(sorted_dict)):
        for j in range(len(sorted_dict[i])):
            real_labels[sorted_dict[i][j]] = favorite
            favorite += 1

    return real_labels

def pairwise_interaction_Viol(ids, real_labels):
    priority_rank = [ids[0]] 
    for i in range(1, len(ids)):
        current_id = ids[i]
        inserted = False
        for j in range(len(priority_rank)):
            count += 1
            if real_labels[current_id] < real_labels[priority_rank[j]]:
                priority_rank.insert(j, current_id)
                inserted = True
                break

      
        if not inserted:
            priority_rank.append(current_id)

    priority_rank.reverse()
    priority_rank = {id: rank + 1 for rank, id in enumerate(priority_rank)}
    priority_rank = dict(sorted(priority_rank.items(), key=lambda item: item[1]))
    return priority_rank, count

def pairwise_annotation(ids, real_labels):
    priority_rank = [ids[0]]
    count = 0
    for i in range(1, len(ids)):
        current_id = ids[i]
        low, high = 0, len(priority_rank) - 1
        while low <= high:
            mid = (low + high) // 2
            if real_labels[current_id] < real_labels[priority_rank[mid]]:
                high = mid - 1
                count += 1
            else:
                low = mid + 1
                count += 1
        priority_rank.insert(low, current_id)
    priority_rank.reverse()
    priority_rank = {id: rank + 1 for rank, id in enumerate(priority_rank)}
    priority_rank = dict(sorted(priority_rank.items(), key=lambda item: item[1]))
    return priority_rank, count

def X_and_y(data, sampled_ids, priority_rank):
    y = []
    x = []
    for i, value in priority_rank.items():
        y.append(priority_rank[i])
        x.append(data[i])
    X = np.array(x, dtype=np.float32)
    y = np.array(y, dtype=np.float32)
    return X, y



def predict(model, new_data):
    new_data = np.array(new_data).reshape(1, -1)
    prediction = model.predict(new_data)
    return prediction[0]

def truth_topk_get(real_labels, top_k):
    sorted_keys = sorted(real_labels, key=real_labels.get)
    top_k_true = sorted_keys[:top_k]
    return top_k_true

def top_k_keys(dictionary, k):
    sorted_items = sorted(dictionary.items(), key=lambda item: item[1])
    top_k = [item[0] for item in sorted_items[:k]]
    return top_k

def topk_accuracy(true, predict, k):
    true_topk = set(true[:k])
    predict_topk = set(predict[:k])
    correct = len(true_topk.intersection(predict_topk))
    accuracy = correct / k
    return accuracy

def random_test(cluster,budget,k,ndata,sortedCluster):
    n_clusters=cluster
    n_budget = budget
    top_k = k
    data=ndata
    n_sortedCluster=sortedCluster
    real_labels = human_annotation_stim(data, n_clusters=n_clusters)
    sampled_data_ids, sampled_data, test_data_ids= active_learning(data, n_sortedCluster,n_budget)
    priority_rank, count1 = pairwise_annotation(sampled_data_ids, real_labels)

    priority_rank1, count2 = pairwise_interaction_Viol(sampled_data_ids, real_labels)
    X, y = X_and_y(data, sampled_data_ids, priority_rank)
    model = train_random_forest(X, y)
    real_labels = {key: value for key, value in real_labels.items() if key not in sampled_data_ids}
    top_k_true = truth_topk_get(real_labels, top_k)
    results = {}
    x=len(test_data_ids)
    print(x)
    for index in test_data_ids:
        result = predict(model, new_data=data[index])
        results[index] = result
    top_k_predict = top_k_keys(results, top_k)
    top_k_size=sizePattern.size_pattern(top_k)
    accuracy_p = topk_accuracy(top_k_true, top_k_predict, top_k)
    # print(real_labels)
    # print(sampled_data_ids)
    # print(test_data_ids)
    # print(top_k_true)
    # print(top_k_predict)
    print(accuracy_p)
    # print(accuracy_s)
    print(results)
    return accuracy_p,count1,count2
