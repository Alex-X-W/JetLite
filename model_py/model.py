'''
A toy model which takes in a matrix of shape (2, 2), and adds 1 to each element then outputs
'''

import tensorflow as tf
import numpy as np

input = tf.placeholder(tf.int32, (2, 2), 'input')
bias = tf.Variable(np.array([[1, 1], [1, 1]], dtype=np.int32), name='bias')
output = tf.add(input, bias, 'output')

init = tf.global_variables_initializer()

sess = tf.Session(tf.get_default_graph())
sess.run(init)
sess.run(output, feed_dict={input: np.array([[1, 2], [3, 4]], dtype=np.int32)})

# create a Saver object as normal in Python to save your variables
saver = tf.train.Saver()

saver_def = saver.as_saver_def()
print(saver_def.filename_tensor_name)
print(saver_def.restore_op_name)

# write out 3 files
saver.save(sess, 'trained_model.sd')
tf.train.write_graph(sess.graph_def, '.', 'trained_model.proto', as_text=False)
tf.train.write_graph(sess.graph_def, '.', 'trained_model.txt', as_text=True)