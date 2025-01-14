
clear all

% Histogram equalization

% A black and white image
x = imread('plaza.bmp');
figure(1),imshow(x);
figure(2),imhist(x), axis 'tight';

% Equalization
[y,t] = histeq(x);
figure(3),imshow(y);
figure(4),imhist(y), axis 'tight';
figure(5),plot(0:255,round(t*255)); axis([0 255 0 255]);

%% A color image
% original image
flor_orig = double(imread('flor_orig.png'))/255;
figure(6),imshow(flor_orig);
% low contrast version
flor = double(imread('flor_lc.png'))/255;
figure(7),imshow(flor);

% Equalization of red/gren/blue chanels separately
r = flor(:,:,1); % RED channel of image
g = flor(:,:,2); % GREEN channel of image
b = flor(:,:,3); % BLUE channel of image

flor_eq1 = zeros(size(flor));
flor_eq1(:,:,1) = histeq(r);
flor_eq1(:,:,2) = histeq(g);
flor_eq1(:,:,3) = histeq(b);

figure(8),imshow(flor_eq1);


% Representation by luminance and chrominance
flor_ycbcr = rgb2ycbcr(flor);

y  = flor_ycbcr(:,:,1); % Y channel of image
cb = flor_ycbcr(:,:,2); % Cb channel of image
cr = flor_ycbcr(:,:,3); % Cr channel of image

% Equalization of luminance
z = zeros(size(flor));
z(:,:,1) = histeq(y);
z(:,:,2) = cb;
z(:,:,3) = cr;

flor_eq2 = ycbcr2rgb(z);
figure(9),imshow(flor_eq2);


%% ***** Analysis of a DFT *****

% a rectangle
image_rec = imread('rectangle.bmp');
figure(10),imshow(image_rec,[]);
[H,W] = size(image_rec)

% its DFT
TR_image_rec = fft2(double(image_rec));
TR_image_rec = fftshift(TR_image_rec);
figure(11); colormap(jet);
imagesc(log(abs(TR_image_rec)))
axis image; title('{\bf Magnitude of the DFT}');

% rectangle multiplied by a cosine function
for m=1:H
	for n=1:W
	   image_rec_cos(m,n) = double(image_rec(m,n)) * cos(2*pi*m/8);
	end
end
figure(12); imshow(image_rec_cos,[]);

% its DFT
TR_image_rec_cos = fft2(double(image_rec_cos));
TR_image_rec_cos = fftshift(TR_image_rec_cos);
figure(13); colormap(jet);
imagesc(log(abs(TR_image_rec_cos)))
axis image; title('{\bf Magnitude of the DFT}')

%% a new image: a car
image_car = imread('car.bmp');
figure(14),imshow(image_car,[]);
[M,N] = size(image_car)

% its DFT
TR_image_car = fft2(double(image_car));
TR_image_car = fftshift(TR_image_car);
figure(15); colormap(jet);
imagesc(log(abs(TR_image_car)))
axis image; title('{\bf Magnitude of the DFT}')
figure(16); colormap(jet);
imagesc(angle(TR_image_car))
axis image; title('{\bf Phase of the DFT}')

%% DFT masking

% an image with a background pattern
FileName= 'LenaText.png';
[LenaTx] = imread(FileName);
[M,N]=size(LenaTx)
figure(17),imshow(LenaTx),axis image, title('Lena with background pattern');

% its DFT
TLenaTx = fft2(LenaTx);
figure(18),imagesc(log(abs(fftshift(TLenaTx)))), axis image, title('DFT of Lena with background pattern');

% DFT mask
TLenaMask = TLenaTx;
FCenter=77;
MediaBanda=5;
for i=FCenter-MediaBanda:FCenter+MediaBanda
    TLenaMask(i,1)=0;
    TLenaMask(258-i,1)=0;
   for j=2:MediaBanda+1
        TLenaMask(i,j)=0;
        TLenaMask(i,258-j)=0;  
        TLenaMask(258-i,j)=0;
        TLenaMask(258-i,258-j)=0;
    end
    for j=FCenter-MediaBanda:FCenter+MediaBanda
        TLenaMask(i,j)=0;
        TLenaMask(i,258-j)=0;  
        TLenaMask(258-i,j)=0;
        TLenaMask(258-i,258-j)=0;
    end
end
for j=FCenter-MediaBanda:FCenter+MediaBanda
    TLenaMask(1,j)=0;
    TLenaMask(1,258-j)=0;
    for i=2:MediaBanda+1
        TLenaMask(i,j)=0;
        TLenaMask(i,258-j)=0;  
        TLenaMask(258-i,j)=0;
        TLenaMask(258-i,258-j)=0;
    end
end
 
% showing the result
% the DFT with the mask
figure(19);imagesc(fftshift(log(abs(TLenaMask)))); axis image;
title('{\bf Fourier Transform multiplied by the mask }');
% the enhanced image
LenaMask = mat2gray(real(ifft2((TLenaMask))));
figure(20),imshow(LenaMask),axis image,title('Lena with Fourier Transf. mask');

