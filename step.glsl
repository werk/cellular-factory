#version 300 es
precision mediump float;
precision highp int;

uniform highp usampler2D state;
uniform int step;
out uint outputValue;


const uint NOT_FOUND = 4294967295u;

struct Material {
    uint material;
    uint WEIGHT;
    uint HEAT;
};

const uint AIR = 0u;
const uint WATER = 5u;
const uint SAND = 30u;
const uint LAVA = 65u;

const uint AIR_WEIGHT_SIZE = 10u;
const uint WATER_WEIGHT_SIZE = 10u;
const uint SAND_WEIGHT_SIZE = 10u;

const uint AIR_HEAT_SIZE = 5u;
const uint WATER_HEAT_SIZE = 5u;
const uint SAND_HEAT_SIZE = 5u;


uint encode(Material material) {
    uint traits = 0u;
    switch(material.material) {
        case AIR:
            traits = material.WEIGHT + AIR_WEIGHT_SIZE * (material.HEAT);
            break;
        case WATER:
            traits = material.WEIGHT + WATER_WEIGHT_SIZE * (material.HEAT);
            break;
        case SAND:
            traits = material.WEIGHT + SAND_WEIGHT_SIZE * (material.HEAT);
            break;
        case LAVA:
            traits = material.HEAT;
            break;
        default:
            traits = - material.material;
    }
    return material.material + traits;
}

Material decode(uint integer) {
    Material material;
    material.WEIGHT = NOT_FOUND;
    material.HEAT = NOT_FOUND;
    if(integer < WATER) {
        material.material = AIR;
        uint trait = integer - AIR;
        uint WEIGHT_offset = AIR_HEAT_SIZE;
        material.WEIGHT = trait / WEIGHT_offset;
        uint WEIGHT_remainder = trait - (material.WEIGHT * WEIGHT_offset);
        material.HEAT = WEIGHT_remainder;
    } else if(integer < SAND) {
        material.material = WATER;
        uint trait = integer - WATER;
        uint WEIGHT_offset = WATER_HEAT_SIZE;
        material.WEIGHT = trait / WEIGHT_offset;
        uint WEIGHT_remainder = trait - (material.WEIGHT * WEIGHT_offset);
        material.HEAT = WEIGHT_remainder;
    } else if(integer < LAVA) {
        material.material = SAND;
        uint trait = integer - SAND;
        uint WEIGHT_offset = SAND_HEAT_SIZE;
        material.WEIGHT = trait / WEIGHT_offset;
        uint WEIGHT_remainder = trait - (material.WEIGHT * WEIGHT_offset);
        material.HEAT = WEIGHT_remainder;
    } else {
        material.material = LAVA;
        uint trait = integer - LAVA;
        material.HEAT = trait;
    }
    return material;
}

Material lookupMaterial(vec2 offset) {
    uint integer = texture(state, offset/* / scale*/).r;
    return decode(integer);
}

bool rule_FallDown(inout Material pp_0_0, inout Material pp_0_1) {
    uint n = pp_0_0.WEIGHT;
    uint m = pp_0_1.WEIGHT;
    if(n == NOT_FOUND) return false;
    if(m == NOT_FOUND) return false;
    if(n <= m) return false;
    return true;
}

bool rule_WaveLeft(inout Material pp_0_0, inout Material pp_1_0) {
    if(pp_0_0.material != AIR) return false;
    if(pp_1_0.material != WATER) return false;
    return true;
}

void main() {

    vec2 position = gl_FragCoord.xy - 0.5;
    vec2 offset = mod(float(step), 2.0) == 0.0 ? vec2(-1.0, -1.0) : vec2( 0.0,  0.0);
    vec2 bottomLeft = floor((position + offset) * 0.5) * 2.0 - offset;

    // Read and parse relevant pixels
    Material pp_0_0 = lookupMaterial(bottomLeft + vec2(0.0, 0.0));
    Material pp_0_1 = lookupMaterial(bottomLeft + vec2(0.0, 1.0));
    Material pp_1_0 = lookupMaterial(bottomLeft + vec2(0.1, 0.0));
    Material pp_1_1 = lookupMaterial(bottomLeft + vec2(0.1, 1.0));
    Material pm_0_1 = lookupMaterial(bottomLeft + vec2(0.0, -1.0));
    Material pm_1_1 = lookupMaterial(bottomLeft + vec2(0.1, -1.0));

    // Fall
    bool did_Fall = false;
    bool did_FallDown = false;
    if(true) {
        did_FallDown = did_FallDown || rule_FallDown(pp_0_0, pp_0_1);
        did_FallDown = did_FallDown || rule_FallDown(pp_1_0, pp_1_1);
        did_Fall = did_Fall || did_FallDown;
    }

    // Wave
    bool did_Wave = false;
    bool did_WaveLeft = false;
    if(!did_Fall) {
        did_WaveLeft = did_WaveLeft || rule_WaveLeft(pp_0_0, pp_1_0);
        did_WaveLeft = did_WaveLeft || rule_WaveLeft(pp_0_1, pp_1_1);
        did_Wave = did_Wave || did_WaveLeft;
    }

    // Write and encode own material
    vec2 quadrant = position - bottomLeft;
    Material target = pp_0_0;
    if(quadrant == vec2(0.0, 0.1)) target = pp_0_1;
    else if(quadrant == vec2(1.0, 0.0)) target = pp_1_0;
    else if(quadrant == vec2(1.0, 0.1)) target = pp_1_1;
    outputValue = encode(target);

    if(step == 0) outputValue = uint((cos(gl_FragCoord.x * 13.37 + gl_FragCoord.y * 37.13) + 1.0) * 10.0);
}