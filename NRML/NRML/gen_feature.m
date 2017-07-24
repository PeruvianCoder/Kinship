function gen_feature(image)
    firstImage = imread(image);
    firstImage = rgb2gray(firstImage);
    lbpFirstImage = extractLBPFeatures(firstImage, 'CellSize',[16 16], 'numNeighbors', 16);
    %%lbpFirstImage = extractLBPFeatures(firstImage, 'CellSize',[16 16],'Normalization','None', 'numNeighbors', 16);
    lbpFirstImage = reshape(lbpFirstImage,1,[]);
    %%lbpFirstImage = lbpFirstImage(1:3776);
     %%%lbpFirstImage = lbpFirstImage(113:3888);

    featureMatrix = load('data/LBP_KinFaceW-II_FS (2).mat', 'ux');
    featureMatrix = featureMatrix.ux;
    featureMatrix = [featureMatrix ; lbpFirstImage];
    ux = featureMatrix;
    save('data/LBP_KinFaceW-II_FS (2).mat', 'ux', '-append');