precision mediump float;

uniform float t;

void main() {
    gl_FragColor = vec4(sin(t * 13.37) * 0.5 + 0.5, 1, 1, 1);
}