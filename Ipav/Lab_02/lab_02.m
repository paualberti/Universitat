clear all

% Uniform quantization of audio signals

FileName='music.wav';          % Define the filename
[y,Fs] = audioread(FileName);    % Read a stereo wav file
x=(y(:,1)+y(:,2))/2;           % Add left/right channels to get a mono sound

interval= 90000:100000;        % Define a temporal interval 
figure(1); plot(interval,x(interval)); axis tight; % Plot the signal in the interval

sound(x,Fs); pause                  % Listen to audio

Nbits = 5;                          % Number of bits
Delta = 2/(2^Nbits -1);             % Compute the quantization step
quantized1=round(x/Delta) * Delta;  % Quantize

figure(2); plot(interval,x(interval),'-.',interval,quantized1(interval),'x')
legend('Original signal','Quantized signal'); title('Signal quantization')
figure(3); plot(interval,x(interval),interval,x(interval)-quantized1(interval));
title('Quantization Error')

% Listen to the quantized sound
sound(quantized1,Fs); pause

% Compute SNR
10*log10((x'*x)/((x-quantized1)'*(x-quantized1)))

% Plot the SNR as a function of the number of bits.
for Nbits = 1:16;                         % Number of bits
    Delta = 2/(2^Nbits -1);               % Compute the quantization step
    quantized1=round(x/Delta) * Delta;    % Quantize
    SNR_a(Nbits)=10*log10((x'*x)/((x-quantized1)'*(x-quantized1)));   % actual SNR
    SNR_t(Nbits)=6*Nbits+4.77-20*log10(1/std2(x));                    % theoretical SNR
end
figure(4),plot(1:16,SNR_a,'b-',1:16,SNR_t,'g-'); grid; axis 'tight';
legend('Actual SNR','Theoretical SNR'); title('SNR as a function of Nbits');

% Histogram of quatization error
Nbits =14;                            % Number of bits
Delta = 2/(2^Nbits -1);               % Compute the quantization step
quantized1=round(x/Delta) * Delta;    % Quantize
figure(5); hist(x-quantized1,100)     % Display the error histogram

%Listen to the quantized sound changing the number of quantization bits:
Nbits =14;                           % Number of bits
Delta = 2/(2^Nbits -1);              % Compute the quantization step
quantized1=round(x/Delta) * Delta;   % Quantize
sound(quantized1,Fs)
SNR=10*log10((x'*x)/((x-quantized1)'*(x-quantized1)))

%% Mu-Law quantization of speech signals

[x,Fs] = audioread('speech.wav'); % Read the speech wav file
interval = 5550:49930;
r = max(abs(x));                % Dynamic range of the signal 
sound(x(interval),Fs); pause;

% Signal histogram
figure(6); hist(x(interval),100);

% Representation of compressor and expander functions
y = -1:0.01:1;
u = 255;
C = sign(y).*log(1+u*abs(y))/log(1+u);     % u-law compressor
figure(8), plot(y,C),grid;title ('u-law Compressor');
E = sign(y).*((1+u).^abs(y)-1)/u;          % u-law expander
figure(9), plot(y,E),grid;title ('u-law Expander');

% SNRseg as a function of Nbits and the level of signal
s = x(interval);
C = sign(s).*log(1+u*abs(s))/log(1+u);   % u-law compressor
for Nbits = 2:15;                  % Number of bits
    Delta = 2/(2^Nbits -1);        % Compute the quantization step
    qU = round(s/Delta) * Delta;   % Quantize Uniform
    q = round(C/Delta) * Delta;    % Quantize u-law
    E = sign(q).*((1+u).^abs(q)-1)/u; % u-law expander 
    SNR_UNIFORM(Nbits-1)=snr_seg(s,qU,Fs*20/1000);
    SNR_ULAW(Nbits-1)=snr_seg(s,E,Fs*20/1000);    
end
figure(10);hold on; plot(2:15,SNR_UNIFORM,'r'); plot(2:15,SNR_ULAW,'b');
legend('SNRseg (uniform quantizer)','SNRseg (u-law quantizer)');
title('SNRseg for the LOW power signal'); grid on;

s = 4.5*x(interval);
C = sign(s).*log(1+u*abs(s))/log(1+u);   % u-law compressor
for Nbits = 2:15;                  % Number of bits
    Delta = 2/(2^Nbits -1);        % Compute the quantization step
    qU = round(s/Delta) * Delta;   % Quantize Uniform
    q = round(C/Delta) * Delta;    % Quantize u-law
    E = sign(q).*((1+u).^abs(q)-1)/u; % u-law expander 
    SNR_UNIFORM(Nbits-1)=snr_seg(s,qU,Fs*20/1000);
    SNR_ULAW(Nbits-1)=snr_seg(s,E,Fs*20/1000);
end
figure(11);hold on; plot(2:15,SNR_UNIFORM,'r'); plot(2:15,SNR_ULAW,'b');
legend('SNRseg (uniform quantizer)','SNRseg (u-law quantizer)');
title('SNRseg for the HIGH power signal'); grid on;

% Histogram of quantization noise for Nbits=8 as a function of signal
% dynamic range
% Parameters
Nbits=8;
Delta = 2/(2^Nbits -1);              % Compute the quantization step
gain=[1,4.5];                        % Factors of amplification
                   
p=0;
figure(12);
for i = 1:size(gain,2); 
    s = gain(i)*x(interval);                 % the signal is amplified
% Quantization
    qU = round(s/Delta) * Delta;             % Quantize Uniform
    C = sign(s).*log(1+u*abs(s))/log(1+u);   % u-law compressor
    q = round(C/Delta) * Delta;              % Quantize u-law
    E = sign(q).*((1+u).^abs(q)-1)/u;        % u-law expander 
 % Display the error histogram    
    p=p+1;
    noise_sigma = std2(s-qU);
    subplot(2,2,p),hist(s-qU,100);
    title(['Uniform, gain=',num2str(gain(i)),', noise sigma=',num2str(noise_sigma)]); 
    p=p+1;
    noise_sigma = std2(s-E);
    subplot(2,2,p),hist(s-E,100);
    title(['u-law, gain=',num2str(gain(i)),', noise sigma=',num2str(noise_sigma)]);
end

%% Image quantization
image_lena = imread('lena.bmp');
figure(13); imshow(image_lena,[]);

Nbits = 4;                          			% Number of bits
Delta = 255/(2^Nbits -1);                       % Compute the quantization step
image_lena_quantized = uint8(round(double(image_lena)/Delta)*Delta);  % Quantize
figure(13); imshow(image_lena_quantized,[]);

error = double(image_lena)-double(image_lena_quantized);
10*log10(255^2 / std2(error)^2)

% Color bar image
gradsize = 2; 
barsize = round(256*gradsize/7);
bar = kron(0:255,ones(barsize,gradsize)); % scale bar line
bar0 = zeros(barsize,256*gradsize);       % zero bar line
gradients(:,:,1) = uint8([bar;bar;bar0;bar0;bar;bar;bar0]); % RED component
gradients(:,:,2) = uint8([bar;bar0;bar;bar0;bar;bar0;bar]); % GREEN component
gradients(:,:,3) = uint8([bar;bar0;bar0;bar;bar0;bar;bar]); % BLUE component
figure(14), imshow(gradients); title('ORIGINAL');

% Color image quantization
Nbits = 5; % Number of bits
Delta = 255/(2^Nbits -1); % Compute the quantization step 

% quantize green and blue
g = uint8(round(double(gradients(:,:,2))/Delta)*Delta); % Quantize G
b = uint8(round(double(gradients(:,:,3))/Delta)*Delta); % Quantize B

gradients_quantized_g = gradients; gradients_quantized_g(:,:,2) = g;
figure(15),imshow(gradients_quantized_g),title('GREEN QUANTIZATION');
gradients_quantized_b = gradients; gradients_quantized_b(:,:,3) = b;
figure(16),imshow(gradients_quantized_b),title('BLUE QUANTIZATION');



