#include <iostream>

#include "i_ucount.h"

using namespace std;

int output(uint32_t unumber, uint32_t uvalue, void *p_data)
{
	cout << "unumber: " << unumber << " uvalue: " << uvalue << endl;
	return 0;
}

int main()
{
	i_ucount *p_ucount_1 = NULL;
	i_ucount *p_ucount_2 = NULL;
	create_ucount_instance(&p_ucount_1);
	create_ucount_instance(&p_ucount_2);
	p_ucount_1->init("./aaa", i_ucount::CREATE, 0600);
	p_ucount_2->init("./bbb", i_ucount::CREATE, 0600);

	for (int i = 0; i != 3; ++i) {
		cout <<	p_ucount_1->set(i * 4096 * 8, i_ucount::SET) << endl;
	}
	
	for (int i = 0; i != 3; ++i) {
		cout <<	p_ucount_2->set(i * 4096 * 8, i_ucount::SET) << endl;
	}
	
	for (int i = 0; i != 3; ++i) {
		cout <<	p_ucount_1->get(i * 4096 * 8) << endl;
	}
	
	for (int i = 0; i != 3; ++i) {
		cout <<	p_ucount_2->get(i * 4096 * 8) << endl;
	}
		
	cout <<	p_ucount_1->get() << endl;
	cout <<	p_ucount_2->get() << endl;

	p_ucount_1->merge(p_ucount_2, i_ucount::UNION);

	p_ucount_1->traverse(output, NULL, 1);
	p_ucount_2->traverse(output, NULL, 1);

	p_ucount_1->uninit();
	p_ucount_2->uninit();
	p_ucount_1->release();
	p_ucount_2->release();

	return 0;
}

