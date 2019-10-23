#include <stdio.h>
#include <stdint.h>
#include <map>
#include <string.h>
#include <stdlib.h>
#include <limits.h>

using namespace std;

static uint8_t code_n[] = {
    0,1,1,2,1,2,2,3,1,2,2,3,2,3,3,4,
    1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,
    1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,
    2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
    1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,
    2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
    2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
    3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,
    1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,
    2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
    2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
    3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,
    2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,
    3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,
    3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,
    4,5,5,6,5,6,6,7,5,6,6,7,6,7,7,8,
};

class code {
    //friend bool code_comp(const code& a, const code& b);

    private:
        int n;
        uint32_t* data;

        static uint32_t distance(uint32_t i) {
            uint32_t count=0;
            uint8_t* p = (uint8_t*)&i;
            for(int j=0; j<4; j++) {
                count += code_n[p[j]];
            }
            return count;
        }

    public:
        code()  {
            n = 0;
            data = NULL;
        }

        //code(const code& o) {
        //    this->n = o.n;
        //    this->data = (uint32_t*)malloc(o.n * sizeof(uint32_t));
        //    memcpy(this->data, o.data, o.n * sizeof(uint32_t));
        //}

        code(const char* s) {
            int l = strlen(s);
            n = 0;
            char* c = (char*)s;
            while((c = strchr(c, 'x')) != NULL) {
                n++;
                c++;
            }

            data = (uint32_t*)malloc(n * sizeof(uint32_t));
            c = (char*)s;
            for(int i=0; i<n; i++) {
                c = strchr(c, 'x') - 1;
                sscanf(c, "%x", &data[i]);
                c += 2;
            }
        }

        ~code() {
            if(data != NULL) {
                free(data);
                data = NULL;
                n = 0;
            }
        }

        //bool operator==(const code& o) {
        //    if(this->n != o.n)   return false;

        //    for(int i=0; i<this->n; i++) {
        //        if(this->data[i] != o.data[i])  return false;
        //    }
        //    return true;
        //}

        void print() {
            if(this->n != 0) {
                for(int i=0; i<this->n; i++) {
                    printf(" 0x%x", this->data[i]);
                }
            }
        }

        uint32_t distance(code& o) {
            uint32_t ret = 0;
            int this_start = 0;
            int o_start = 0;
            int cnt = 0;
            if(this->n > o.n) {
                o_start = 0;
                this_start = this->n - o.n;
                cnt = o.n;
                for(int i=0; i<this_start; i++) {
                    ret += distance(this->data[i]);
                }
            } else if(this->n < o.n) {
                this_start = 0;
                o_start = o.n - this->n;
                cnt = this->n;
                for(int i=0; i<o_start; i++) {
                    ret += distance(o.data[i]);
                }
            } else {
                cnt = this->n;
            }

            for(int i=0; i<cnt; i++) {
                ret += distance(this->data[this_start+i] ^ o.data[o_start+i]);
            }

            //this->print();
            //printf("<-> ");
            //o.print();
            //printf(" = %d\n", ret);
            return ret;
        }


};

//bool code_comp(const code& a, const code& b) {
//    if(a.n < b.n) {
//        return true;
//    } else {
//        return false;
//    }
//}

void clean(map<code*, int>& mCode) {
    while(mCode.size() != 0) {
        map<code*, int>::iterator it = mCode.begin();
        mCode.erase(it);
        delete(it->first);
    }
}

void train(map<code*, int>& mCode, const char* file) {
    FILE* f = fopen(file, "r");
    if(f == NULL) {
        printf("open %s failed.\n", file);
        return;
    }
    char buf[1024];
    char type[1024];
    char code_str[2][1024];
    code_str[0][0] = 0;
    code_str[1][0] = 0;
    int index = 0;
    int lastindex = 1;
    int keep = 0;
    int lost = 0;
    int cnt;

    while(fgets(buf, 1024, f) != NULL) {
        sscanf(buf, "%d %[^:]:%s", &cnt, &code_str[index][0], type);
        if(strcmp(code_str[index], code_str[lastindex]) == 0) {
        } else if(code_str[lastindex][0] != 0){
            code* c = new code(code_str[lastindex]);
            //if(keep > lost) {
            //    mCode.insert(pair<code*, int>(c, keep));
            //} else {
            //    mCode.insert(pair<code*, int>(c, -lost));
            //}
            mCode.insert(pair<code*, int>(c, keep-lost));
            keep = 0;
            lost = 0;
        }

        if(strcmp(type, "keep") == 0) {
            keep += cnt;
        } else {
            lost += cnt;
        }

        index = (index+1)%2;
        lastindex = (lastindex+1)%2;
    }

    for(map<code*, int>::iterator it = mCode.begin(); it != mCode.end(); it++) {
        it->first->print();
        printf(":%d\n", it->second);
    }

    fclose(f);
}

void classify(map<code*, int>& mCode, const char* classify, const char* instances) {
    FILE* fc = fopen(classify, "r");
    if(fc == NULL) {
        printf("open %s failed.\n", classify);
        return;
    }
    char buf[1024];
    char code_str[1024];
    int weights;

    while(fgets(buf, 1024, fc) != NULL) {
        sscanf(buf, "%[^:]:%d", code_str, &weights);
        code* c = new code(code_str);
        mCode.insert(pair<code*, int>(c, weights));
    }

    fclose(fc);

    FILE* fi = fopen(instances, "r");
    if(fi == NULL) {
        printf("open %s failed.\n", instances);
        return;
    }

    int distance;
    int ret;
    while(fgets(buf, 1024, fi) != NULL) {
        code c(buf);
        weights = 0;
        distance = INT_MAX;
        for(map<code*, int>::iterator it = mCode.begin(); it != mCode.end(); it++) {
            ret = it->first->distance(c);
            if(ret < distance) {
                weights = it->second;
                distance = ret;
            } else if(ret == distance) {
                weights += it->second;
            }
        }

        c.print();
        printf(":%s\n", weights>0?"keep":"lost");
    }

    fclose(fi);

}

void help(const char* name) {
    printf("Usage: %s [config]\n", name);
    printf("\t--train=[t] trainset\n");
    printf("\t--classify=[c] classifier instances\n");
}

int main(int args, char* argv[]) {
    //                                     |                               |                               |                               |
    //code c("0xaaabaaa 0xaaaaaa2a 0x0 0x0");
    //map<code, int, bool(*)(const code&, const code&)> mCode(code_comp);
    if(args < 3) {
        help(argv[0]);
        return -1;
    }
    map<code*, int> mCode;
    if(strcmp(argv[1], "--train") == 0 ||
            strcmp(argv[1], "-t") == 0) {
        train(mCode, argv[2]);
    } else if(strcmp(argv[1], "--help") == 0 ||
            strcmp(argv[1], "-h") == 0) {
        help(argv[0]);
    } else if(strcmp(argv[1], "--classify") == 0 ||
            strcmp(argv[1], "-c") == 0) {
        if(args < 4) {
            help(argv[0]);
            return -1;
        }
        classify(mCode, argv[2], argv[3]);
    }

    clean(mCode);
    return 0;
}
