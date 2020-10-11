#version 300 es
precision mediump float;

out uint outputColor;
uniform float t;

void main() {
    outputColor = uint((sin(t * 13.37) + 1.0) * 100.0);
}