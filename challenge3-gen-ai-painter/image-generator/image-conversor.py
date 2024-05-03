import math
import tensorflow as tf
import os

from PIL import Image

IMAGES_PER_FILE = 60
SIZE_IMG = 256
IMAGE_SOURCE = '/home/equipemeia04/Documents/identifying-artists/images/images/Salvador_Dali'
IMAGE_DEST = '/home/equipemeia04/Documents/identifying-artists/tfrecord-converted'
FILE_DEST = 'paintings-[N].tfrec'

## Resize mode:
## 1: Keep all the painting: the larger dimension (width or height) is converted to the target size 256 while the 
## smaller one is resized keeping the ratio between the larger one and the target to not cause any distortion. The reminder
## region in the smaller dimension is filled with white pixels.
## 2: Cut some part of the painting: the smaller dimension (width or height) is converted to the target size 256 while the 
## largest one is resized keeping the ratio between the smaller one and the target to not cause any distortion. Since the largest
## dimension is still greather than our target size, the program will "centralize" the image cutting some image portions in the begin
## and in the end.   
RESIZE_MODE = 2 

img_counter = 0
file_counter = 1

def create_new_file(image_dest, file_counter):
    file_name = image_dest.replace("[N]", str(file_counter).rjust(2, "0"))       
    print('new file: ' + file_name)
    tfrecord_writer = tf.io.TFRecordWriter(image_dest.replace("[N]", str(file_counter).rjust(2, "0")))
    return tfrecord_writer

def resize_image(img_path, img_path_resized):
    target_size = [256,256]
    with Image.open(img_path) as img:
        if (RESIZE_MODE == 1): 
            # Calculate the new size while maintaining the aspect ratio
            original_width, original_height = img.size
            ratio = min(target_size[0] / original_width, target_size[1] / original_height)
            new_size = (int(original_width * ratio), int(original_height * ratio))

            # Resize the image
            resized_img = img.resize(new_size)

            # Create a blank white background image
            result_img = Image.new("RGB", target_size, (255, 255, 255))
            
            # Paste the resized image onto the center of the blank background
            result_img.paste(resized_img, ((target_size[0] - new_size[0]) // 2, (target_size[1] - new_size[1]) // 2))
        elif RESIZE_MODE == 2:
            # Calculate the new size while maintaining the aspect ratio
            original_width, original_height = img.size
            ratio = max(target_size[0] / original_width, target_size[1] / original_height)
            new_size = (math.ceil(original_width * ratio), math.ceil(original_height * ratio))
            
            # Resize the image
            resized_img = img.resize(new_size)

            # Create a blank white background image
            result_img = Image.new("RGB", target_size, (255, 255, 255))
            
            # Paste the resized image onto the center of the blank background
            result_img.paste(resized_img, ((target_size[0] - new_size[0]) // 2, (target_size[1] - new_size[1]) // 2))
        else:
            raise Exception("Not implemented")
        
        # Save the result image
        result_img.save(img_path_resized)
    
tfrecord_writer = create_new_file(os.path.join(IMAGE_DEST, FILE_DEST), file_counter)

for name in os.listdir(IMAGE_SOURCE):
    if tfrecord_writer == None:
        tfrecord_writer = create_new_file(os.path.join(IMAGE_DEST, FILE_DEST), file_counter)
        
    img_path = os.path.join(IMAGE_SOURCE, name)
    img_path_resized = os.path.join(IMAGE_DEST, name)

    resize_image(img_path, img_path_resized)

    try:
        raw_file = tf.io.read_file(img_path_resized)
    except FileNotFoundError:
        print("Couldn't read file  {}".format(img_path_resized))
        continue

    example = tf.train.Example(features=tf.train.Features(feature={
        #'image_name': tf.io.FixedLenFeature([], tf.string),
       'image': tf.train.Feature(bytes_list=tf.train.BytesList(value=[raw_file.numpy()])),
        #'target': tf.io.FixedLenFeature([], tf.string),
    }))
    print(img_path)
    tfrecord_writer.write(example.SerializeToString())

    if img_counter == IMAGES_PER_FILE:    
        img_counter = 0
        file_counter = file_counter + 1
        tfrecord_writer.close()
        tfrecord_writer = None
        
    img_counter = img_counter + 1 

if tfrecord_writer != None:
    tfrecord_writer.close()
        