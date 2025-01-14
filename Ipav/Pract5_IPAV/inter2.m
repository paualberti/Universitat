function y = inter2(x)

x2 = zeros(2*size(x));
x2(1:2:end,1:2:end) = x;
x3 = x2(1:end-1,1:end-1);
h = [1/4 1/2 1/4;1/2 1 1/2;1/4 1/2 1/4];
y = imfilter(x3,h);