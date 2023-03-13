#version 410

layout (location=0) in vec3 position;


uniform mat4 mv_matrix;
uniform mat4 p_matrix;
uniform sampler2D s;


out vec4 varyingColor;

void main(void)
{
	gl_Position = p_matrix * mv_matrix * vec4(position,1.0);
	
	varyingColor = vec4(position,1.0)*0.5 + vec4(0.5, 0.5, 0.5, 0.5);
} 
