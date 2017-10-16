precision mediump float;

uniform sampler2D u_TextureUnit;
uniform bool u_DrawingSelected;
uniform vec4 u_LineColor;

varying vec2 v_TextureCoordinates;

void main() {
	if(u_DrawingSelected) {
		gl_FragColor = u_LineColor;
	}
	else {
		gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
	}
}