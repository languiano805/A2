#version 410

in vec4 varyingColor;
out vec4 color;

uniform mat4 mv_matrix;
uniform mat4 p_matrix;

void main(void)
{	color = varyingColor;

}