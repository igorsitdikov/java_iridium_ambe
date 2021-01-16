class Iridium
{
private:
    /* data */
public:
    Iridium();
    ~Iridium();

    void decode(const char *input, int in_size, char *output, int out_size);
};