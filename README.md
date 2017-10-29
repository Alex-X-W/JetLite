### A Skeleton Showing How to Use TF Model in JAVA
#### 0. Requirements
* Tensorflow >= 1.3
* Maven

#### 1. Train and Save a Model in Python with TF
please go to `model_py/model.py` for a toy example

#### 2. Freeze the Model
basically what this step does is to convert all trained variables into constant tensors, so that when you later on load the model in Java, you don't need to initialize the variables.  
please see `model_py/freeze.py`

#### 3. Load in Java and Run
you still need to prepare your inference data and feed in.  
please see `src/main/java/edu/nyu/jetlite/tf_intergration.java`.