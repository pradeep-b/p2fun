# p2fun
## Bundle com.ibm.pradeepb.p2.sitesgc
The bundle com.ibm.pradeepb.p2.sitesgc adds a menu and a button on the toolbar. The very first time the button is clicked, it will cache the repos (ie, initialization). From the next time onwards, each time it is clicked, it will remove the repos that were added after it was initialized.

This is to investigate whether there are APIs to get all entries and remove entries from "Available Software Sites" pref store. There is a related forum posting [1].

This is required because when WTP accesses adapters from the internet, those sites get added to this list which shouldn't be (see [2]). So, if there is a p2 API to fetch remote repositories without adding the URI to the "Available Software Sites" list, that should help too. In the absence of that we will need to deal with the pref store directly (via APIs). This query was posted on the forum [3] but no responses so far.

* [1] https://www.eclipse.org/forums/index.php/t/38712/
* [2] https://bugs.eclipse.org/bugs/show_bug.cgi?id=487917
* [3] https://www.eclipse.org/forums/index.php/m/1726988/#msg_1726988


