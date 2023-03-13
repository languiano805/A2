#version 410

//in vec4 varyingColor;
out vec4 color;
in vec2 tc;



uniform mat4 mv_matrix;
uniform mat4 p_matrix;
uniform sampler2D s;

void main(void)
{	color = texture(s, tc);

}
