
clear all;

FileName='music.wav';          % Define the filename
[y,Fx] = audioread(FileName);    % Read a stereo wav file
x=(y(:,1)+y(:,2))/2; 
sound(x,Fx); pause;            % Listen to audio

% Experiment parameters 
T=10;                   % Signal length
M=4;                    % Decimation factor
mt=1;                   % FT analysis time
window=480;             % FT analysis window length
NDFT=512;               % Number of DFT samples
f=[0:1/NDFT:0.5];
NF=[1:NDFT/2+1];

%%  original 1D signal

[S,g,t,p] = spectrogram(x(1:T*Fx),window,0,NDFT,Fx,'yaxis'); 
figure(1),surf(t,f*Fx,20*log10(abs(S)),'EdgeColor','none');
title 'Original signal spectrogram'; axis xy; axis tight; colormap(jet); view(0,90);
xlabel('Time'); ylabel('Frequency (Hz)');

sx=x(mt*Fx:mt*Fx+window-1);
Tsx=fft(sx,NDFT);
figure(2),plot(f*Fx,20*log10(abs(Tsx(NF)))),axis tight;
title(['FT at t = ',num2str(mt),' s of the original signal']);
xlabel('Frequency (Hz)'); ylabel('FT 20*log(magnitude)');

%% Decimation of an 1D signal
%  decimation with filter  

y=decimate(x(1:T*Fx),M,'FIR');
Fy=Fx/M;
sound(y,Fy); pause;

sy=y(mt*Fy:mt*Fy+window/M-1);
Tsy=fft(sy,NDFT);
figure(3),plot(f*Fy,20*log10(abs(Tsy(NF)))),axis tight;
title(['FT at t = ',num2str(mt),' s of the filtered and decimate signal']);
xlabel('Frequency (Hz)'); ylabel('FT 20*log(magnitude)');

% decimation with no filter 

z=downsample(x(1:T*Fx),M);
Fz=Fx/M;
sound(z,Fz); pause;

sz=z(mt*Fz:mt*Fz+window/M-1);
Tsz=fft(sz,NDFT);
figure(4),plot(f*Fz,20*log10(abs(Tsz(NF)))),axis tight;
title(['FT at t = ',num2str(mt),' s of the decimated signal']);
xlabel('Frequency (Hz)'); ylabel('FT 20*log(magnitude)');


%% Interpolation of an 1D signal

% The interpolator filter
h=[ ];                              % Impulse response of the Lagrange interpolator
N=2;                                % Interpolation factor

Th=fft(h,NDFT);
figure(5),plot(f,abs(Th(NF))),axis tight,title 'Lagrange cubic interpolator frequency response'; 
xlabel('Frequency'); ylabel('Frequency response magnitude');

% One zero sample is placed between every two original samples
Fv=Fy*N;
v=upsample(y(1:T*Fy),N);
sound(v,Fv); pause;

sv=v(mt*Fv:mt*Fv+window/M*N-1);
Tsv=fft(sv,NDFT);
figure(6),plot(f*Fv,20*log10(abs(Tsv(NF)))),axis tight;
title(['FT at t = ',num2str(mt),' s of the signal interpolated without filter']);
xlabel('Frequency (Hz)'); ylabel('FT 20*log(magnitude)');

% Lagrange interpolated signal 
Fw=Fy*N;
w=conv(v,h,'valid');
sound(w,Fw); pause;

sw=w(mt*Fw:mt*Fw+window/M*N-1);
Tsw=fft(sw,NDFT);
figure(7),plot(f*Fw,20*log10(abs(Tsw(NF)))),axis tight;
title(['FT at t = ',num2str(mt),' s of the interpolated signal']);
xlabel('Frequency (Hz)'); ylabel('FT 20*log(magnitude)');



