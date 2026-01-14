uniform float2 resolution;
uniform float time;
uniform float2 mouse; // Ready for interaction
uniform float intensity_factor; // Default 1.0

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution;
    float ratio = resolution.x / resolution.y;
    float2 p = (uv * 2.0 - 1.0) * float2(ratio, 1.0);

    float t = time * 0.15;

    // Multi-layered organic wave motion
    float z = 0.0;
    for(float i = 1.0; i <= 3.0; i++) {
        p.x += 0.3 / i * sin(i * 3.0 * p.y + t + i * 0.5);
        p.y += 0.2 / i * cos(i * 2.5 * p.x + t * 0.8 + i * 0.3);
        z += abs(p.x) * abs(p.y);
    }

    // Color Palette: Deep Blue, Soft Purple, Cyan Glow
    half3 color1 = half3(0.02, 0.05, 0.15); // Deep Blue
    half3 color2 = half3(0.25, 0.15, 0.45); // Soft Purple
    half3 color3 = half3(0.1, 0.4, 0.5);   // Cyan Glow

    // Generative mixing based on coordinates and time
    float noise = sin(p.x * 2.0 + t) * cos(p.y * 2.0 - t * 0.5);
    half3 finalColor = mix(color1, color2, 0.5 + 0.5 * sin(z + t));
    finalColor = mix(finalColor, color3, smoothstep(0.2, 0.8, noise * 0.5 + 0.5));

    // Vignette and OLED black dominance
    float vignette = smoothstep(1.2, 0.2, length(p * 0.6));
    finalColor *= vignette;

    // Very subtle grain/texture for premium feel (faked via high-freq sine)
    float grain = sin(fragCoord.x * 10.0) * sin(fragCoord.y * 10.0) * 0.02;
    finalColor += grain;

    return half4(finalColor, 1.0);
}
