--                                ID,                                         KEY,               PARENT_ID,                                  PARENT_KEY, CATEGORY,     TYPE,       DOMAIN,     VALID_IN_DOMAIN,  CREATED,           MODIFIED,          NAME,                             DESCRIPTION,                                           PRIORITY, SERVICE_LEVEL, APPLICATION_ENTRY_POINT, CUSTOM_1 - 8
-- MASTER CLASSIFICATIONS
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000001', 'L10000',          '',                                         '',        'EXTERNAL',   'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'OLD-Leistungsfall',               'OLD-Leistungsfall',                                   999,     'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000002', 'L10303',          '',                                         '',        'EXTERNAL',   'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Beratungsprotokoll',              'Beratungsprotokoll',                                  1,       'P2D',         '',                      'VNR,RVNR,KOLVNR, ANR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000003', 'L1050',           '',                                         '',        'EXTERNAL',   'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Widerruf',                        'Widerruf',                                            1,       'P3D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000004', 'L11010',          '',                                         '',        'EXTERNAL',   'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Dynamikänderung',                 'Dynamikänderung',                                     7,       'P4D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000005', 'L110102',         'CLI:000000000000000000000000000000000004', 'L11010',  'EXTERNAL',   'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Dynamik-Ablehnung',               'Dynamik-Ablehnung',                                   5,       'P5D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000006', 'L110105',         'CLI:000000000000000000000000000000000004', 'L11010',  'EXTERNAL',   'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Dynamik-Ausschluss',              'Dynamik-Ausschluss',                                  5,       'P5D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000007', 'L110107',         'CLI:000000000000000000000000000000000004', 'L11010',  'EXTERNAL',   'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Dynamik-Einschluss/Änd.',         'Dynamik-Einschluss/Änd.',                             5,       'P6D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000008', 'L12010',          '',                                         '',        'EXTERNAL',   'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Gewährung-Policendarlehen',       'Gewährung-Policendarlehen',                           8,       'P7D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000009', 'L140101',         '',                                         '',        'EXTERNAL',   'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Zustimmungserklärung',            'Zustimmungserklärung',                                9,       'P8D',         '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000010', 'T2100',           '',                                         '',        'MANUAL',     'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'T-Vertragstermin VERA',           'T-Vertragstermin VERA',                               2,       'P10D',        '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000011', 'T6310',           '',                                         '',        'AUTOMATIC',  'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'T-GUK Honorarrechnung erstellen', 'Unterstützungskasse Honorar wird fällig',             2,       'P11D',        '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000013', 'DOCTYPE_DEFAULT', '',                                         '',        'EXTERNAL',   'DOCUMENT', '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'EP allgemein',                    'EP allgemein',                                        99,      'P2000D',      '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000017', 'L1060',           '',                                         '',        'EXTERNAL',   'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Widerruf neu',                    'Widerruf neu',                                        1,       'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000018', 'A12',             'CLI:000000000000000000000000000000000001', 'L10000',  'MANUAL',     'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Beratungsprotokoll',              'Beratungsprotokoll',                                  1,       'P1D',         '',                      'VNR,RVNR,KOLVNR, ANR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000019', 'T21001',          'CLI:000000000000000000000000000000000020', 'L19001',  'MANUAL',     'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Widerruf',                        'Widerruf',                                            1,       'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000020', 'L19001',          '',                                         '',        'EXTERNAL',   'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Beratungszustimmung',             'Beratungszustimmung',                                 1,       'P1D',         '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000022', 'A13',             'CLI:000000000000000000000000000000000011', 'T6310',   'AUTOMATIC',  'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Beratungsprotokoll',              'Beratungsprotokoll',                                  1,       'P1D',         '',                      'VNR,RVNR,KOLVNR, ANR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000023', 'T2000',           '',                                         '',        'MANUAL',     'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'T-Vertragstermin',                'T-Vertragstermin',                                    1,       'P1D',         'z',                     'VNR,KOLVNR,RVNR', 'CUSTOM2', 'Custom3', 'custom4', 'custom5', 'custom6', 'custom7', 'custom8');
INSERT INTO CLASSIFICATION VALUES('CLI:000000000000000000000000000000000024', 'T2001',           '',                                         '',        'MANUAL',     'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'T-Vertragstermin',                'T-Vertragstermin',                                    1,       'P0D',         'z',                     'VNR,KOLVNR,RVNR', 'CUSTOM2', 'Custom3', 'custom4', 'custom5', 'custom6', 'custom7', 'custom8');
INSERT INTO CLASSIFICATION VALUES('CLI:300000000000000000000000000000000017', 'L3060',           '',                                         '',        'EXTERNAL',   'TASK',     '',         FALSE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Widerruf neu',                    'Widerruf neu',                                        1,       'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');

-- DOMAIN_A CLASSIFICATIONS
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000002', 'L10303',          '',                                         '',        'EXTERNAL',   'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Beratungsprotokoll',              'Beratungsprotokoll',                                  101,      'P2D',         '',                      'VNR,RVNR,KOLVNR, ANR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000003', 'L1050',           '',                                         '',        'EXTERNAL',   'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Widerruf',                        'Widerruf',                                            1,        'P13D',        '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000004', 'L11010',          '',                                         '',        'EXTERNAL',   'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Dynamikänderung',                 'Dynamikänderung',                                     1,        'P14D',        '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000005', 'L110102',         'CLI:100000000000000000000000000000000004', 'L11010',  'EXTERNAL',   'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Dynamik-Ablehnung',               'Dynamik-Ablehnung',                                   5,        'P15D',        '',                      'VNR,RVNR,KOLVNR', 'TEXT_1', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000006', 'L110105',         'CLI:100000000000000000000000000000000004', 'L11010',  'EXTERNAL',   'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Dynamik-Ausschluss',              'Dynamik-Ausschluss',                                  5,        'P16D',        '',                      'VNR,RVNR,KOLVNR', 'TEXT_2', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000007', 'L110107',         'CLI:100000000000000000000000000000000004', 'L11010',  'EXTERNAL',   'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Dynamik-Einschluss/Änd.',         'Dynamik-Einschluss/Änd.',                             5,        'P5D',         'point0815',             'VNR,RVNR,KOLVNR', 'TEXT_1', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000008', 'L12010',          '',                                         '',        'EXTERNAL',   'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Gewährung-Policendarlehen',       'Gewährung-Policendarlehen',                           1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000009', 'L140101',         '',                                         '',        'EXTERNAL',   'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Zustimmungserklärung',            'Zustimmungserklärung',                                2,        'P2D',         '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000010', 'T2100',           '',                                         '',        'MANUAL',     'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'T-Vertragstermin VERA',           'T-Vertragstermin VERA',                               2,        'P2D',         '',                      'VNR', 'cust2', 'cust3', 'cust4', 'cust5', 'cust6', 'cust7', 'cust8');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000011', 'T6310',           '',                                         '',        'AUTOMATIC',  'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'T-GUK Honorarrechnung erstellen', 'Unterstützungskasse Honorar wird fällig',             2,        'P2D',         'point0815',             'VNR', 'custom2', 'custom3', 'custom4', 'custom5', 'custom6', 'custom7', 'custom8');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000013', 'DOCTYPE_DEFAULT', '',                                         '',        'EXTERNAL',   'DOCUMENT', 'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'EP allgemein',                    'EP allgemein',                                        99,       'P2000D',      '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000014', 'L10000',          '',                                         '',        'EXTERNAL',   'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'BUZ-Leistungsfall',               'BUZ-Leistungsfall',                                   1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', 'VNR', 'VNR', 'VNR', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000016', 'T2000',           '',                                         '',        'MANUAL',     'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'T-Vertragstermin',                'T-Vertragstermin',                                    1,        'P1D',         'z',                     'VNR,KOLVNR,RVNR', 'CUSTOM2', 'Custom3', 'custom4', 'custom5', 'custom6', 'custom7', 'custom8');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000024', 'T2001',           '',                                         '',        'MANUAL',     'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'T-Vertragstermin',                'T-Vertragstermin',                                    1,        'P0D',         'z',                     'VNR,KOLVNR,RVNR', 'CUSTOM2', 'Custom3', 'custom4', 'custom5', 'custom6', 'custom7', 'custom8');
INSERT INTO CLASSIFICATION VALUES('CLI:100000000000000000000000000000000017', 'L1060',           '',                                         '',        'EXTERNAL',   'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Widerruf neu',                    'Widerruf neu',                                        1,        'P1D',         'specialPoint',          'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:400000000000000000000000000000000017', 'L3060',           '',                                         '',        'EXTERNAL',   'TASK',     'DOMAIN_A', FALSE,           RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Widerruf neu',                    'Widerruf neu',                                        1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');

-- DOMAIN_B CLASSIFICATIONS
INSERT INTO CLASSIFICATION VALUES('CLI:200000000000000000000000000000000015', 'T2100',           '',                                         '',        'MANUAL',     'TASK',     'DOMAIN_B', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'T-Vertragstermin VERA',           'T-Vertragstermin VERA',                               22,       'P2D',         '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:200000000000000000000000000000000017', 'L1060',           '',                                         '',        'EXTERNAL',   'TASK',     'DOMAIN_B', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Widerruf neu',                    'Widerruf neu',                                        1,        'P1D',         'point0816',             'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:200000000000000000000000000000000018', 'L19001',          '',                                         '',        'EXTERNAL',   'TASK',     'DOMAIN_B', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Beratungszustimmung',             'Beratungszustimmung',                                 1,        'P1D',         '',                      'VNR', '', '', '', '', '', '', '');

-- WITH PARENT CLASSIFICATIONS (MIXED DOMAIN) ---
-- DOMAIN_A
INSERT INTO CLASSIFICATION VALUES('CLI:200000000000000000000000000000000001', 'A12',             'CLI:100000000000000000000000000000000014', 'L10000',  'EXTERNAL',   'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'OLD-Leistungsfall',               'OLD-Leistungsfall',                                   1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:200000000000000000000000000000000002', 'A13',             'CLI:100000000000000000000000000000000011', 'T6310',   'AUTOMATIC',  'TASK',     'DOMAIN_A', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Beratungsprotokoll',              'Beratungsprotokoll',                                  1,        'P1D',         '',                      'VNR,RVNR,KOLVNR, ANR', '', '', '', '', '', '', '');
-- DOMAIN_B
INSERT INTO CLASSIFICATION VALUES('CLI:200000000000000000000000000000000003', 'A12',             'CLI:200000000000000000000000000000000018', 'L19001',  'MANUAL',     'TASK',     'DOMAIN_B', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Widerruf',                        'Widerruf',                                            1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO CLASSIFICATION VALUES('CLI:200000000000000000000000000000000004', 'T21001',          'CLI:200000000000000000000000000000000018', 'L19001',  'MANUAL',     'TASK',     'DOMAIN_B', TRUE,            RELATIVE_DATE(0) , RELATIVE_DATE(0) , 'Beratungsprotokoll',              'Beratungsprotokoll',                                  1,        'P1D',         '',                      'VNR,RVNR,KOLVNR, ANR', '', '', '', '', '', '', '');
