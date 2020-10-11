precision mediump float;
uniform sampler2D state;

uniform float t;

void main() {
    vec2 offset = vec2(0, 0);
    vec2 resolution = vec2(500, 500);
    float zoom = 1.0;
    float screenToMapRatio = zoom / resolution.x;
    vec2 xy = gl_FragCoord.xy * screenToMapRatio + offset;
    vec2 tile = floor(xy + 0.5);

    vec2 stateSize = vec2(100, 100);

    vec4 center = texture2D(state, tile / stateSize);

    gl_FragColor = vec4(center.x, sin(t) * 0.5 + 0.5, 1, 1);
}