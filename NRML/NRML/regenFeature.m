images = dir('C:\Users\CarlosGri\Downloads\KinFaceW-II\KinFaceW-II\images\father-son\*.jpg');
for image = images'
    % jpgfile = load(image.name);
    gen_feature(strcat('C:\Users\CarlosGri\Downloads\KinFaceW-II\KinFaceW-II\images\father-son\' , image.name))
end 