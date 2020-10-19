#version 300 es
precision mediump float;
precision highp int;

uniform highp usampler2D state;
uniform int step;

out uint outputColor;

void main() {
    outputColor = uint((cos(gl_FragCoord.x * 13.37 + gl_FragCoord.y * 37.13) + 1.0) * 10.0);
    /*
    vec2 position = gl_FragCoord.xy - 0.5;
    vec2 offset =
        mod(float(step), 2.0) == 0.0
        ? vec2(-1.0, -1.0)
        : vec2( 0.0,  0.0);

    vec2 bottomLeft = position; //floor((position + offset) * 0.5) * 2.0 - offset;
    //vec2 bottomRight = bottomLeft + vec2(1.0, 0.0);
    //vec2 topLeft = bottomLeft + vec2(0.0, 1.0);
    //vec2 topRight = bottomLeft + vec2(1.0, 1.0);

    vec2 stateSize = vec2(100, 100);
    uvec4 bottomLeftValue = texture(state, bottomLeft / stateSize);

    if(step <= 1) {
        uint sum = uint(bottomLeft.x) + uint(bottomLeft.y);
        outputColor = sum % 2u == 0u ? 0u : 1u;
    } else {
        outputColor = (bottomLeftValue.r > 0u) ? 0u : 1u;
    }
    */
}