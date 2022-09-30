
from flask import Flask, redirect, request, Response
import base64
import io
from flask_cors import CORS
from werkzeug.serving import run_simple
from personal_color_analysis import personal_color
from PIL import Image

app = Flask(__name__)
CORS(app)



@app.route("/sendFrame", methods=['POST'])
def re():
    print(request.method)
    if request.method == 'POST':
        # 안드로이드에서 'image'변수에 base64로 변환된 bitmap이미지
        one_data = request.form['image']

        # base64로 인코딩된 이미지 데이터를 디코딩하여 byte형태로 변환
        imgdata = base64.b64decode(one_data)

        # byte형태의 이미지 데이터를 이미지로 변환
        image = Image.open(io.BytesIO(imgdata))



        imagepath = './face_img_from_Server/testUpload6.jpg'
        image.save(imagepath, 'JPEG')
        # 이미지 분석관련 코드 작성
        #personal_color.analysis(imagepath)

        personal_res = personal_color.analysis(imagepath)

        return personal_res

    #return personalColor

    # 결과값 리턴
    #return 'test'
    #return 'true'


if __name__ == "__main__":
    app.run(debug=True)
    #run_simple('0.0.0.0', 5000, app)
    #app.run()



