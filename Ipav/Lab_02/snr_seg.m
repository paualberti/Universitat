function s = snr_seg(x,y,interval)

% x: input signal
% y: output signal
% interval: segments in samples

[x_s z] = buffer(x,interval);
[y_s z] = buffer(y,interval);

snr = [];
for i = 1:size(x_s,2),
    x1 = x_s(:,i);
    y1 = y_s(:,i);
    snr = [snr 10*log10((x1'*x1)/((x1-y1)'*(x1-y1)))];
end;
        
s = mean(snr);        