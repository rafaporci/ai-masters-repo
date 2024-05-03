import tensorflow as tf
import numpy as np
from PIL import Image
import base64
from io import BytesIO
import os
import random


def load_cycle_gan_model(painter):
    try:
        print(f"Loading {painter} model...")

        #model_path = os.path.join(
        #    "C:\\", "Projects", "meia", "MEIA1s-pprog-engcia",
        #    "challenge3", "image-generator", "notebooks", "models", painter,
        #    "cycle_gan_model"
        #)
        
        model_path = os.path.join(
             "C:\\", "DataSets", "kera-models", painter,
             "cycle_gan_model"
        )
        
        #model_path = os.path.join(
        #    "C:\\", "dev", "models", painter,
        #    "cycle_gan_model"
        #)

        loaded_cycle_gan_model = tf.saved_model.load(model_path)
        return loaded_cycle_gan_model
    except Exception as e:
        print(f"Error loading CycleGAN model for {painter}: {e}")
        return None

def load_models():
    print('Loading models...')

    models = {
        "caravaggio": load_cycle_gan_model("caravaggio"),
        "monet": load_cycle_gan_model("monet"),
        "picasso": load_cycle_gan_model("picasso"),
        "dali": load_cycle_gan_model("dali")
    }
    
    return models


def generate_image(painter, style, image_file, models):
    # Dictionary mapping styles to painters
    style_to_painter = {
        "baroco": "caravaggio",
        "barroco": "caravaggio",
        "impressionism": "monet,picasso",
        "abstract_expressionism": "dali",
    }

    # Dictionary mapping painters to styles
    painter_to_style = {
        "caravaggio": ["barroco"],
        "monet": ["impressionism"],
        "picasso": ["impressionism"],
        "dali": ["abstract_expressionism"]
    }

    resultImages = []

    # Process the image
    img = Image.open(image_file)
    img = img.resize((256, 256))  # Resize to match CycleGAN input size
    img = np.array(img)
    img = (img - 127.5) / 127.5  # Normalize the image
    img = tf.convert_to_tensor(img, dtype=tf.float32)  # Convert to tf.float32

    # Add batch dimension
    img = tf.expand_dims(img, axis=0)

    if painter is None:
        style_painters = style_to_painter.get(style)
        if style_painters is None:
            return {"error": f"No painter found for style '{style}'", "image": None}

        for painter_x in style_painters.split(','):
            cycle_gan_model = models[painter_x]
            if cycle_gan_model is None:
                return {"error": f"Error loading CycleGAN model for {style}", "image": None}

            try:
                prediction = cycle_gan_model.monet_generator(img, training=False)[0].numpy()
                prediction = (prediction * 127.5 + 127.5).astype(np.uint8)

                image_pil = Image.fromarray(prediction)
                buffered = BytesIO()
                image_pil.save(buffered, format="JPEG")
                img_str = base64.b64encode(buffered.getvalue()).decode()

                resultImages.append({
                    "style": style,
                    "paintedBy": painter_x,
                    "image": img_str
                }) 
            except Exception as e:
                print(f"Error processing image: {e}")
                return {"error": "Error processing image", "image": None}
    else:
        if painter in painter_to_style:
            painter_styles = painter_to_style[painter]
            for painter_style in painter_styles:
                cycle_gan_model = models[painter]
                if cycle_gan_model is None:
                    return {"error": f"Error loading CycleGAN model for {painter_style}", "image": None}

                try:
                    prediction = cycle_gan_model.monet_generator(img, training=False)[0].numpy()
                    prediction = (prediction * 127.5 + 127.5).astype(np.uint8)

                    image_pil = Image.fromarray(prediction)
                    buffered = BytesIO()
                    image_pil.save(buffered, format="JPEG")
                    img_str = base64.b64encode(buffered.getvalue()).decode()

                    resultImages.append({
                        "style": painter_style,
                        "paintedBy": painter,
                        "image": img_str
                    }) 
                except Exception as e:
                    print(f"Error processing image: {e}")
                    return {"error": "Error processing image", "image": None}
        else:
            return {"error": f"Painter '{painter}' not found in painter_to_style mapping", "image": None}

    return resultImages



def generate_image_reverse(image_file, models, painter="Not Applicable", style="Not Applicable"):
    # Dictionary mapping styles to painters
    style_to_painter = {
        "baroco": "caravaggio",
        "barroco": "caravaggio",
        "impressionism": "monet,picasso",
        "abstract_expressionism": "dali",
    }

    # Dictionary mapping painters to styles
    painter_to_style = {
        "caravaggio": ["barroco"],
        "monet": ["impressionism"],
        "picasso": ["impressionism"],
        "dali": ["abstract_expressionism"]
    }

    resultImages = []

    # Process the image
    img = Image.open(image_file)
    img = img.resize((256, 256))  # Resize to match CycleGAN input size
    img = np.array(img)
    img = (img - 127.5) / 127.5  # Normalize the image
    img = tf.convert_to_tensor(img, dtype=tf.float32)  # Convert to tf.float32

    # Add batch dimension
    img = tf.expand_dims(img, axis=0)

    cycle_gan_model = models["monet"]
    if cycle_gan_model is None:
        return {"error": f"Error loading CycleGAN model for {style}", "image": None}

    try:
        prediction = cycle_gan_model.photo_generator(img, training=False)[0].numpy()
        prediction = (prediction * 127.5 + 127.5).astype(np.uint8)

        image_pil = Image.fromarray(prediction)
        buffered = BytesIO()
        image_pil.save(buffered, format="JPEG")
        img_str = base64.b64encode(buffered.getvalue()).decode()

        resultImages.append({
            "style": "",
            "paintedBy": "",
            "image": img_str
        }) 
    except Exception as e:
        print(f"Error processing image: {e}")
        return {"error": "Error processing image", "image": None}


    return resultImages