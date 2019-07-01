# Glossary

## Product

What are we building?
- auto-reconciliation
- invoicing
- payment ability
- client portal/view
- money movement
- report generation
- notifications/alerts
- features that replicate their current mobile application (online banking)

## Transactions (TX/tx/TXN/txn)

**ACH** - *Automated Clearing House Transfer* - ACH transfers does not occur on the same day. It is next or future day settlement. Very check for the bank and free for the account holder.

__Life Cycle__

**Wire** - *Wire Transfer* - Wire transfers are guaranteed funds represent by a single transaction which may resolve on the same business day. Slight more expensive and does cost the account holder a small fee. Where do the fees come out of? Can it be from another account?

**Check** - Check is in reference to both single check or a deposit slip which may encompass many checks. Deposit slips on the backend show as a single transaction which another query may be required to extract out the checks that it is composed of.

__Life Cycle__

Account holder writes check.
Checks expire after 90 days.
Canceled
At some point in time this check may finally be cashed in which the funds are then withdrawn from that account. Improper account can cause a check to bounce of the account to get overdrawn.

**eCheck** - Electronic check. Printed or purely electronic?

**Credit Card** - Instant funds out of your account?

**Transfers**

Internal Transfers - Instant? How are funds moved internally in the bank? If there are fees who eats the cost?

External Transfers - Probably not instant - To other banks maybe ACH? If there are fees who eats the cost?

## Terms

**Lighthouse** - Code name of internal initiative to ramp up their technology efforts

**Test Region** - Infrastructure within the bank to hold data that is exposed to our backend. Idea is that this region is only exposed to us with minor risk (some data scrubbed or not exported) and if compromised can be easily shut down.

**Reconciliation** - Depending on practicing state, Law firms are require to send an Reconciliation Report to their state bar. This report shows the funds going in and out of the IOLTA account and whos money is whos.

**Receiveables**

**Bill pay** - internal mechanism to either pay a bill through ACH or echeck. Non-customizable, if ACH is available

**Source Data** - transactions have originating source data about the transaction, ie a check deposit may have the originating account/routing number. Source data seems to be coming in after the transaction have been posted.

**Invoice** - also called a bill, which may or may not have a outstanding amount the person who the invoice is addressed to will have to pay. For an invoice to the IOLTA account, the outstanding amount a client has to pay should be 0.

**SSO** - *Single sign on*

**On Prem** - *On premises* - in the context of deployment, our services can either be deployed to the cloud or on the physical premises of the client

**GTM** - *Go to market*

**IOLA/IOLTA** - *Interest on Lawyer (Trust) Account* - regulated and required bank account by all law firms to hold their client's funds, such as retainers and/or settlements

**Operating** - *Operating Account* - business checking account

**KYC** - *Know your customer* - KYC when opening a bank account

**Matter** - in the context of law, an identifier for a case. Some resources found state that the matter is assigned by the accounting department, but from PoC work we've seen anything can go here.

**ABA** - *American Bar Association*

**Quickbooks** - A popular accounting software

**Lawpay** - Payment processing service for lawfirms

