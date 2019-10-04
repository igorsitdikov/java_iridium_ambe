class Iridium
{
private:
    /* data */
public:
    Iridium();
    ~Iridium();

    void to_decode(const char *input, int in_size, char *output, int out_size);
};