# python -m pip install Flask
# python -m pip install flask-cors
# flask --app server run
from flask import Flask, request, jsonify
import flask
from flask_cors import cross_origin
from imagegenerator.main import generate_image
from imagegenerator.main import generate_image_reverse
from imagegenerator.main import load_models

app = Flask(__name__)
models = {}

@app.route("/generate-image", methods=['POST'])
@cross_origin()
def handle_image():
    if 'image' not in request.files:
        return jsonify({"error": "Missing 'image' in request"}), 400

    painter = request.form.get('painter', None)
    style = request.form.get('style', None)
    image_file = request.files['image']

    result = generate_image(painter, style, image_file, models)
    
    return jsonify(result)


@app.route("/generate-image-reverse", methods=['POST'])
@cross_origin()
def handle_painting():
    if 'image' not in request.files:
        return jsonify({"error": "Missing 'image' in request"}), 400

    image_file = request.files['image']

    result = generate_image_reverse(image_file, models, "Not Applicable","Not Applicable")
    
    return jsonify(result)

if __name__ == '__main__':
    models = load_models()

    print('Starting API...')
    app.run(debug=True)