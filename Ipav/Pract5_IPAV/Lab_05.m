clear all;
close all;

%Decimation of 2D signals
% An artificial image
N=128;                      % Size of the image NxN
F1=40; F2=8;				% DFT position of the wavefront frequencies
X = zeros(N,N);  			% First create an DFT of zeros
X(F2+1,F1+1)=N*N/2;			% Set 2 samples values
X(N-F2+1,N-F1+1) = N*N/2;

x=ifft2(X);				            % Obtain the inverse DFT
x_dec = imresize(x,1/2,'nearest'); 	% Decimate by 2 (without filter)
figure(1), imshow(x,[]); 		    % Represent images
figure(2); imshow(x_dec,[]);

X = fftshift(log(1+abs(fft2(x))));		    % DFT modulus of x 
figure(3),imshow(X,[]);			
X_dec=fftshift(log(1+abs(fft2(x_dec)))); 	% DFT modulus of x_dec
figure(4),imshow(X_dec,[]);			

% A natural image
x = double(imread('barbara.png'));			            % Read image
figure(5),imshow(x,[]); title('Original');	    % Display in window 	
g = fspecial('gaussian',5,0.8);		            % Prepare a low-pass filter
x_low = imfilter(x,g,'replicate'); 		        % Filter the image with the low-pass filter
y_low = imresize(x_low,0.5,'nearest');	        % Create a low pass version of the image
figure(6),imshow(y_low,[]);title('Decimated');	% Decimate the low pass version 

X = fftshift(log(1+abs(fft2(x))));			    % DFT of image
figure(7),imshow(X,[]); title('Original DFT');
Y_low = fftshift(log(1+abs(fft2(y_low)))); 		% DFT of low pass version
figure(8),imshow(Y_low,[]); 
title('Low pass filtered decimated DFT');

y = imresize(x,0.5,'nearest');		            % Decimate by 2 without filter		
figure(9),imshow(y,[]); 
title('Original decimated');
Y = fftshift(log(1+abs(fft2(y))));  		    % DFT of decimated image (no filter)
figure(10),imshow(Y,[]); 
title('Original decimated DFT');

%% Interpolation of a 2D signal

x = double(imread('lena.bmp'));		    % Read Lena image
figure(11), imshow(x,[]); 
x2 = zeros(2*size(x));
x2(1:2:end,1:2:end) = x;		        % Zero-stuffing version (N=2)
figure(12),imshow(x2,[]),truesize; 

X = fftshift(log(1+abs(fft2(x))));		% DFT modulus of the Lena image
figure(13),imshow(X,[]);
X2 = fftshift(log(1+abs(fft2(x2))));    % DFT modulus of the zero-stuffing version
figure(14),imshow(X2,[]); 

x2 = inter2(x);
figure(15),imshow(x2,[]),truesize;
title('Bilinear filter');
x3 = imresize(x,2,'nearest'); x3=x3(1:end-1,1:end-1);
figure(16),imshow(x3,[]),truesize;
title('Nearest neighbor filter');

%% ----------------------------------------------------------------------------------------------------------------------------
close all    % Close all figures and graphics
clear all    % Clear all variables defined in the workspace

% Original image
FileName = 'rice';
x = double(imread([FileName,'.png']))/255;
figure(17),imshow(x,[]),title('Original image');
figure(18),imhist(x), axis 'tight', title('Histogram of the original image');

%% ----------------------------------------------------------------------------------------------------------------------------
% Binarization of the original image
threshold = 0.6;

for m=1:size(x,1);
    for n=1:size(x,2);
        if x(m,n)> threshold;
            z(m,n)= 1;
        else
            z(m,n)=0;
        end
    end
end
figure(19),imshow(z,[])

%% ----------------------------------------------------------------------------------------------------------------------------
% Illumination filter
L=65; N=256;
hi = fspecial('gaussian',L,100);
Hi = fftshift(fft2(hi,N,N));
f  = -0.5:1/N:0.5-1/N;

figure(20),surf(f,f,log(1+abs(Hi)),'EdgeColor','none');
axis tight, set(gca,'YDir','reverse');view(0,90);colorbar; colormap(jet);
title('Log - Magnitude of the illumination filter frequency response');
xlabel('F1'),ylabel('F2');
figure(21),surf(f,f,abs(Hi),'EdgeColor','none');
axis tight, set(gca,'YDir','reverse'); colormap(jet);
title('Magnitude of the illumination filter frequency response');
xlabel('F1'),ylabel('F2');

i = imfilter(x,hi,'replicate');
figure(22),imshow(i); title('Image illumination'); 
mu= mean(mean(i));
y=x-i+mu;
figure(23),imshow(y,[]); title('Processed image');   

%% ----------------------------------------------------------------------------------------------------------------------------
% Binarization of the processed image
figure(24), imhist(y), axis 'tight', title('Histogram of the processed image');
threshold =  0.35;		% *** TO BE DETERMINED ***
for m=1:size(y,1);
    for n=1:size(y,2);
        if y(m,n)> threshold;
            z2(m,n)= 1;
        else
            z2(m,n)=0;
        end
    end
end
figure(25),imshow(z2,[]), title('Binarized image (pre-filtered)');

%% ----------------------------------------------------------------------------------------------------------------------------
% Counting algorithm applied to the binarized image WITHOUT pre-processing
[regions_bin,regions,num]=counting_algorithm(x,z);
sprintf('Number of rice grains without pre-processing =%3d',num)
figure(26), imshow(regions_bin); title('Segmentation without pre-processing');
figure(27), imshow(regions); title('Segmentation without pre-processing');

% Counting algorithm applied to the binarized image WITH pre-processing
[regions_bin2,regions2,num2]=counting_algorithm(x,z2);
sprintf('Number of rice grains with pre-processing =%3d',num2)
figure(28), imshow(regions_bin2); title('Segmentation with pre-processing');
figure(29), imshow(regions2); title('Segmentation with pre-processing');



