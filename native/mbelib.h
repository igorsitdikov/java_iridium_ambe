#ifndef LIBRARY_H
#define LIBRARY_H
#ifdef __cplusplus
extern "C" {
#endif
#include "ambe.h"
#include <string.h>
#include <stdlib.h>

#include <stdio.h>

static const unsigned char wav_hdr[] = {
	/* WAV header */
	'R', 'I', 'F', 'F',		/* ChunkID   */
	0x00, 0x00, 0x00, 0x00,		/* ChunkSize */
	'W', 'A', 'V', 'E',		/* Format    */

	/* Sub chunk: format */
	'f', 'm', 't', ' ',		/* Subchunk1ID         */
	0x10, 0x00, 0x00, 0x00,		/* Subchunk1Size       */
	0x01, 0x00,			/* AudioFormat: PCM    */
	0x01, 0x00,			/* NumChannels: Mono   */
	0x40, 0x1f, 0x00, 0x00,		/* SampleRate: 8000 Hz */
	0x80, 0x3e, 0x00, 0x00,		/* ByteRate: 16k/s     */
	0x02, 0x00,			/* BlockAlign: 2 bytes */
	0x10, 0x00,			/* BitsPerSample: 16   */

	/* Sub chunk: data */
	'd', 'a', 't', 'a',		/* Subchunk2ID   */
	0x00, 0x00, 0x00, 0x00,		/* Subchunk2Size */
};

static uint32_t
le32(uint32_t v)
{
#if __BYTE_ORDER__ == __ORDER_LITTLE_ENDIAN__
    return v;
#else
    return ((v & 0x000000ff) << 24) |
           ((v & 0x0000ff00) << 8) |
           ((v & 0x00ff0000) >> 8) |
           ((v & 0xff000000) >> 24);
#endif
}

static uint16_t
le16(uint16_t v)
{
#if __BYTE_ORDER__ == __ORDER_LITTLE_ENDIAN__
    return v;
#else
    return ((v & 0x00ff) << 8) |
           ((v & 0xff00) >> 8);
#endif
}

void retrn( const char *buf, int in_size, short int * result);

#ifdef __cplusplus
}
#endif
#endif