#version 300 es
precision mediump float;
precision highp int;

uniform highp usampler2D state;
uniform sampler2D materials;
uniform vec2 resolution;
uniform float t;
out vec4 outputColor;

const float tileSize = 12.0;
const vec2 tileMapSize = vec2(4096.0, 256.0);

void main() {
    vec2 stateSize = vec2(100, 100);

    vec2 offset = vec2(0, 0);
    float zoom = 40.0;
    float screenToMapRatio = zoom / resolution.x;
    vec2 xy = gl_FragCoord.xy * screenToMapRatio + offset;
    vec2 tile = floor(xy + 0.5);
    vec2 spriteOffset = mod(xy + 0.5, 1.0) * 12.0;

    uint meterial = texture(state, tile / stateSize).r;
    vec2 tileMapOffset = vec2(float(meterial) * tileSize, tileSize) + spriteOffset * vec2(1, -1);
    vec4 color = texture(materials, tileMapOffset / tileMapSize);

    outputColor = color;
}