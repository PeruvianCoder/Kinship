%% demo: NRML

clear all;
clc;

addpath('nrml');

%% data & parametres
%%load('data\LBP_KinFaceW-II_FS.mat');
load('data/LBP_KinFaceW-II_FS (2).mat');

T = 1;        % Iterations
knn = 5;      % k-nearest neighbors
Wdims = 100;  % low dimension

un = unique(fold);
nfold = length(un);

%%Somehow this supposedly deletes previous saved features??
%%featureMatrix = load('data\LBP_KinFaceW-II_FS.mat', 'ux');
featureMatrix = load('data/LBP_KinFaceW-II_FS (2).mat', 'ux');
featureMatrix = featureMatrix.ux;
[r,c] = size(featureMatrix);

%%reload new LBP feature file
while(r > 500)
    %%fprintf('Greater than 500');
    featureMatrix(501, :) = [];
    [r,c] = size(featureMatrix);
end
ux = featureMatrix;
%%save('data\LBP_KinFaceW-II_FS.mat', 'ux', '-append');
save('data/LBP_KinFaceW-II_FS (2).mat', 'ux', '-append');

%%use new images to create new features into LBP
images = dir('C:\Users\CarlosGri\Documents\kiniship\images\*.jpg');
for image = images'
    gen_feature(strcat('C:\Users\CarlosGri\Documents\kiniship\images\' , image.name))
end
%%load('data/LBP_KinFaceW-II_FS.mat');
load('data/LBP_KinFaceW-II_FS (2).mat');

% for image = images'
%     fname=image.name;
%     [pathstr, name, ext] = fileparts(fname);
%     movefile(strcat('C:\Users\CarlosGri\Documents\kiniship\images\' ,fname), strcat('C:\Users\CarlosGri\Documents\kiniship\images\' ,fullfile(pathstr, [name '.jpg-done'])))
% end

%% NRML
t_sim = [];
t_ts_matches = [];
t_acc = zeros(nfold, 1);
for c = 5:nfold    
    trainMask = fold ~= c;
    testMask = fold == c;    
    tr_idxa = idxa(trainMask);
    tr_idxb = idxb(trainMask);
    tr_matches = matches(trainMask);    
    ts_idxa = idxa(testMask);
    ts_idxb = idxb(testMask);
    ts_matches = matches(testMask);
    
    %% do PCA  on training data
    X = ux;
    tr_Xa = X(tr_idxa, :);                  % training data
    tr_Xb = X(tr_idxb, :);                  % training data
    [eigvec, eigval, ~, sampleMean] = PCA([tr_Xa; tr_Xb]);
    Wdims = size(eigvec, 2);
    X = (bsxfun(@minus, X, sampleMean) * eigvec(:, 1:Wdims));

    tr_Xa_pos = X(tr_idxa(tr_matches), :);  % positive training data 
    tr_Xb_pos = X(tr_idxb(tr_matches), :);  % positive training data
    ts_Xa = X(ts_idxa, :);                  % testing data
    ts_Xb = X(ts_idxb, :);                  % testing data
    %clear X;
    
    %% metric learning
    %%W = nrml_train(tr_Xa_pos, tr_Xb_pos, knn, Wdims, T); 
    trainedMatrix = load('data\NRML-trained-W-marix.mat', 'W');
    W = trainedMatrix.W;
    
    ts_Xa = ts_Xa * W;
    ts_Xb = ts_Xb * W;
    
    %% cosine similarity
    sim = cos_sim(ts_Xa', ts_Xb');
    %%t_sim = [t_sim; sim(:)];
    %%t_sim = sim;
    %%t_ts_matches = [t_ts_matches; ts_matches];

    %% Accuracy
    [~, ~, ~, err, acc] = ROCcurve(sim, ts_matches);
    t_acc(c) = acc;
    %%fprintf('Fold %d, Accuracy = %6.4f \n', c, acc);
end
%%fprintf('The mean accuracy = %6.4f\n', mean(t_acc));

%% extract LBP features
%%testImg = imread('C:\Users\CarlosGri\Downloads\littleCarlos.png');
%%lbpFeatures = extractLBPFeatures(testImg, 'CellSize',[16 16], knn, 16);
%%fprintf('The LBP Features are %d', lbpFeatures);

%%print the similarity result
resultFile = fopen('C:\Users\CarlosGri\Documents\result.txt' , 'w');
fprintf(resultFile, '%6.4f', sim(101));
fprintf('The result is = %6.4f\n', sim(101));
fclose(resultFile);

%% plot ROC
% [fpr, tpr] = ROCcurve(t_sim, t_ts_matches);
% figure(1)
% plot(fpr, tpr);
% xlabel('False Positive Rate')
% ylabel('True Positive Rate')
% grid on;

%%