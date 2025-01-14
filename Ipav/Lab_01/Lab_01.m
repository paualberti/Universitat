
clear all

% Fourier Transform
% Sinusoid parameters
A1 = 1; F1 = 0.125;
A2 = 1; %  System - Create and edit scientific formulas and equations by using Math.
F2 = 0.25; 

% Segment of 32 samples and its DFT
n = transpose(0:31);
xr = A1*cos(2*pi*F1*n) + A2*cos(2*pi*F2*n);
XR = fft(xr,512);
figure(1), plot(abs(XR)); axis tight; grid on;
%%
% Applying the hamming window
vh = hamming(32);
xh = xr.*vh;
XH = fft(xh,512);
figure(2), plot(abs(XH)); axis tight; grid on;


%% Generation and Fourier analysis of a periodic sequence
% The impulse response of the tract model
n = transpose(0:149);
h = (0.95).^n.*cos(2*pi*0.1.*n);
H = fft(h,512);
figure(3);stem(h);
figure(4);plot(abs(H));axis tight; grid on;

% The input signal
e = zeros(1000,1); e(1:60:end) = 1;
figure(5),stem(e), axis tight;

% The system output
y = conv(e,h);

% Output windowing,DFT and representation
y = y(1:840);                       % windowing (rectangular window)
Y = fft(y,1024);
figure(6),stem(y); axis tight; 
figure(7),plot(abs(Y)); axis tight; grid on;


%% Speech generation model
% Signal reading and representation
FileName = 'can.wav';
[a,fs] = audioread(FileName);
figure(8); plot(a); axis tight; grid on;
interval = [197:771];
figure(9); plot(interval,a(interval)); axis tight; grid on;

% Fourier transform of a segment
a_f=fft(a(interval),1024);
figure(10), plot(abs(a_f)); axis tight; grid on;
figure(11), plot(20*log10(abs(a_f))); axis tight; grid on;

% Analysis of a period of the vowel
ha = a(370:449);
HA = fft(ha,1024);
figure(12); plot(abs(HA)); axis tight; grid on;
figure(13); plot(20*log10(abs(HA))); axis tight; grid on;

