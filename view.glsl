#version 300 es
precision mediump float;
precision highp int;

out vec4 outputColor;
uniform highp usampler2D state;
uniform float t;

void main() {
    vec2 offset = vec2(0, 0);
    vec2 resolution = vec2(500, 500);
    float zoom = 1.0;
    float screenToMapRatio = zoom / resolution.x;
    vec2 xy = gl_FragCoord.xy * screenToMapRatio + offset;
    vec2 tile = floor(xy + 0.5);

    vec2 stateSize = vec2(100, 100);

    uvec4 centerInt = texture(state, tile / stateSize);
    float red = float(centerInt.r) / 200.0;

    outputColor = vec4(red, 0/*sin(t) * 0.5 + 0.5*/, 0, 1);
}