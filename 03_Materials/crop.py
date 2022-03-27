import os
import cv2

# haarcascade load
face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_alt.xml')


# 디렉토리 경로를 받아 해당 경로 내의 이미지에 대한 처리 진행
def crop_faces(path, destination):
    print(path)
    fnames = os.listdir(path)
    i = 0
    for fname in fnames:
        i += 1
        if i % 100 == 0:
            print(i)
            #break
        src = os.path.join(path, fname)
        img = cv2.imread(src)
        assert img is not None, f"img={src}: 'No image file....!"
        rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        # 얼굴 찾기
        face = face_cascade.detectMultiScale(rgb, 1.2, 6)
        for (x, y, w, h) in face:
            face_cut = rgb[y:y + h, x:x + w]
            face_cut = cv2.cvtColor(face_cut, cv2.COLOR_BGR2RGB)
            # 얼굴 이미지 저장
            save_path = destination + '/' + fname
            cv2.imwrite(save_path, face_cut)


dataset_path = './temp/dataset'
dst = './dataset'

crop_faces(os.path.join(dataset_path, 'Heart'), os.path.join(dst, 'Heart'))
crop_faces(os.path.join(dataset_path, 'Oblong'), os.path.join(dst, 'Oblong'))
crop_faces(os.path.join(dataset_path, 'Oval'), os.path.join(dst, 'Oval'))
crop_faces(os.path.join(dataset_path, 'Round'), os.path.join(dst, 'Round'))
crop_faces(os.path.join(dataset_path, 'Square'), os.path.join(dst, 'Square'))
