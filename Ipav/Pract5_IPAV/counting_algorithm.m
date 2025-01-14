function [regions_bin,regions,rice_count]=counting_algorithm(x,z)

Labeled_z2 = labelmatrix(bwconncomp(z,4));        % Labeling 4-connected regions

% Preprocessing image before segmentation
se = strel('disk',1);                              % Defines the structuring element for the following openning of erosion
markers = imdilate(imerode(imerode(z,se),se),se);  % Eliminates isolated points and disconnect touching regions

% Segmentation of the image
L = watershed((~markers));         % markers is the initial image from where to start the watershed flooding segmentation

rice_count = max(max(L)); 

limits = L==0;                     % "limits" is the border where two or more watershed regions meets


x1(:,:,1)=z+limits;                % Superpose watersheds limits in yelow and initial grey rice image "y".
x1(:,:,2)=z+limits;
x1(:,:,3)=z;
regions_bin=x1;

x2(:,:,1)=x+limits;                % Superpose watersheds limits in yelow and Original rice image "x".
x2(:,:,2)=x+limits;
x2(:,:,3)=x;
regions=x2;


