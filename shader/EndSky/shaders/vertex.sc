$input a_color0, a_position, a_texcoord0, i_data1, i_data2, i_data3
$output v_color0, v_texcoord0, v_worldPos

#include <bgfx_shader.sh>

uniform mat4 UV0Transform;

void main() {
#ifdef INSTANCING__OFF
    vec4 worldPos = mul(u_model[0], vec4(a_position, 1.0));
#endif
#ifdef INSTANCING__ON
    mat4 model = mtxFromRows(i_data1, i_data2, i_data3, vec4(0.0, 0.0, 0.0, 1.0));
    vec4 worldPos = mul(model, vec4(a_position, 1.0));
#endif
    v_color0 = a_color0;
    v_texcoord0 = mul(UV0Transform, vec4(a_texcoord0, 0.0, 1.0)).xy;
    v_worldPos = a_position;
    gl_Position = mul(u_viewProj, vec4(worldPos.xyz, 1.0));
}
