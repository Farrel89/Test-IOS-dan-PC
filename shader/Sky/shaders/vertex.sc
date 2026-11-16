$input a_color0, a_position, a_texcoord0, i_data1, i_data2, i_data3
$output v_color0, v_texcoord0, v_worldPos

#include <bgfx_shader.sh>

uniform vec4 FogColor;
uniform vec4 SkyColor;

void main() {
#ifdef INSTANCING__OFF
    vec4 worldPos = mul(u_model[0], vec4(a_position, 1.0));
#endif
#ifdef INSTANCING__ON
    mat4 model = mtxFromRows(i_data1, i_data2, i_data3, vec4(0.0, 0.0, 0.0, 1.0));
    vec4 worldPos = mul(model, vec4(a_position, 1.0));
#endif
    v_color0 = mix(SkyColor, FogColor, vec4_splat(a_color0.x));
    v_texcoord0 = a_texcoord0;
    v_worldPos = worldPos.xyz;
    gl_Position = mul(u_viewProj, vec4(worldPos.xyz, 1.0));
}
