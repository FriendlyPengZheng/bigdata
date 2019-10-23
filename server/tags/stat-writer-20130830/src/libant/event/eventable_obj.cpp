#include "event_mgr.hpp"
#include "eventable_obj.hpp"

void
EventableObject::event_remove_event(TimedEvent* ev)
{
	if (ev) {
		assert(m_evs.count(ev));

		ev->deactivate();
		m_evs.erase(ev);
	} else {
		for (EventSet::iterator it = m_evs.begin(); it != m_evs.end(); ++it) {
			(*it)->deactivate();
		}
		m_evs.clear();
	}
}
