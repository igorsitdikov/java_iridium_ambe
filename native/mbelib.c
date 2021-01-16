#ifdef __cplusplus
extern "C" {
#endif
#include "mbelib.h"

void retrn( const char *buffer, int in_size, short int * result)
{
    struct ir77_ambe_decoder *dec = NULL;
    int frames_ok = 0, frames_total = 0;

    int outputsize = in_size - in_size/40;
    char * buf;
    buf = (char *)calloc(outputsize,sizeof(char));;
    /* Init decoder */
    dec = ir77_ambe_decode_alloc();

    int coun = 0;
    for (int i = 0; i < in_size-40; i+=40)
    {
        for (int j = 0; j < 39; j++)
        {
            buf[coun++] = buffer[i+j]; 
        }
    }
    
    /* Process all frames */

    int counterec = 0;

    for (int k = 0; k < outputsize - 39; k += 39)
    {

        uint8_t superframe[39];
        short int audio[2 * 360];
        int rv, i;

        for (int j = 0; j < 39; j++)
        {
            superframe[j] = (uint8_t)buf[k + j];
        }

        /* Skip dummy frames */
        if ((superframe[0] == 0xff) && (superframe[38] == 0xff))
            continue;

        /* Decompress */
        rv = ir77_ambe_decode_superframe(dec, audio, 2 * 360, superframe);
        if (rv < 0)
        {
            fprintf(stderr, "[!] codec error\n");
            break;
        }

        frames_ok += rv;
        frames_total += 2;

        /* Write audio output */
        for (i = 0; i < 2 * 360; i++)
        {
            audio[i] = le16(audio[i]);
            result[counterec++] = le16(audio[i]);
        }
    }
    /* Release decoder */
    ir77_ambe_decode_release(dec);
}
#ifdef __cplusplus
}
#endif