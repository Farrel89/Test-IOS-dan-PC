$input v_color0, v_worldPos, p, fclr, waktu

#include <bgfx_shader.sh>

uniform vec4 FogAndDistanceControl;

float hashAwan(vec2 q) {
    return fract(sin(dot(q, vec2(127.1, 311.7))) * 43758.5453);
}

void main() {
    float day = pow(max(min(1.0 - fclr.r * 1.2, 1.0), 0.0), 0.4);
    float night = pow(max(min(1.0 - fclr.r * 1.5, 1.0), 0.0), 1.2);
    float dusk = clamp(fclr.r - fclr.g, 0.0, 0.5) * 2.0;

    vec2 rf = vec2(0.5 + 20.0 / FogAndDistanceControl.z, 1.0);
    vec2 rt = clamp((FogAndDistanceControl.xy - rf) / (vec2(0.23, 0.7) - rf), 0.0, 1.0);
    float rk = rt.x * rt.y;
    float rain = rk * rk * (3.0 - 2.0 * rk);
    rain *= step(0.01, FogAndDistanceControl.x);

    vec4 diffuse = vec4(v_color0.xyz, v_color0.w);
    float grad = clamp(length(p.xz) * 1.6, 0.1, 1.0);

    float fogSat = max(fclr.r, max(fclr.g, fclr.b)) - min(fclr.r, min(fclr.g, fclr.b));
    float fogLum = dot(fclr.rgb, vec3(0.333, 0.333, 0.333));
    float rainGray = (1.0 - smoothstep(0.05, 0.2, fogSat)) * smoothstep(0.15, 0.4, fogLum);
    float nS = smoothstep(0.3, 0.6, night);
    float rS = smoothstep(0.1, 0.4, max(rain, rainGray)) * (1.0 - nS);
    float duS = smoothstep(0.2, 0.6, dusk) * (1.0 - rS) * (1.0 - nS);
    float pS = smoothstep(0.3, 0.6, day) * (1.0 - rS) * (1.0 - duS) * (1.0 - nS);

    vec4 cPagi = mix(vec4(0.1, 0.5, 1.0, 1.0) * 0.7, vec4_splat(1.0), grad);
    vec4 cSore = mix(vec4(0.1, 0.5, 1.0, 1.0) * 0.2, vec4(1.0, 0.8, 0.1, 1.0), grad);
    vec4 cHujan = mix(vec4(0.3, 0.3, 0.3, 1.0) * 1.5, vec4(0.2, 0.2, 0.2, 1.0), grad);
    vec4 cMalam = mix(vec4(0.1, 0.1, 0.1, 1.0), vec4(0.1, 0.5, 1.0, 1.0), grad);
    diffuse = (cPagi * (pS + 0.0001) + cSore * duS + cHujan * rS + cMalam * nS) / (pS + 0.0001 + duS + rS + nS);

    vec3 dir = normalize(v_worldPos);
    vec2 awBase = dir.xz / max(dir.y, 0.02);
    float awDh = hashAwan(awBase * 999.0);
    float awShd = 0.0;
    float awMm = 1.0;
    float awSc = 1.4;
    for (int i = 0; i < 12; i++) {
        awSc /= 1.009;
        awMm *= 0.96;
        float awan = step(0.88, hashAwan(floor(awBase * awSc + awDh * 0.02 + waktu * 0.04)));
        awShd = mix(awShd, awMm, awan);
    }
    float awanMask = awShd * smoothstep(0.0, 0.12, dir.y) * (1.0 - rS) * (1.0 - nS);
    diffuse.rgb = mix(diffuse.rgb, vec3_splat(1.0), awanMask);

    gl_FragColor = diffuse;
}
