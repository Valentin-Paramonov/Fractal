//#version 120

//in vec2 position;

void main() {
    vec4 vertex = vec4(gl_Vertex);
    gl_Position = gl_ModelViewProjectionMatrix * vertex;
}
