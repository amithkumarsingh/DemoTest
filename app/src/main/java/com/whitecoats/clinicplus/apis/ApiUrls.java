package com.whitecoats.clinicplus.apis;

import static com.whitecoats.clinicplus.MyClinicGlobalClass.productionURL;

import java.util.HashMap;
import java.util.Map;

public class ApiUrls {
    // main production URL

    //  public static String productionURL = "https://arth-seed.whitecoats.com";

    // qa URL


    //  public static String productionURL = "https://qa-arth-seed.whitecoats.com";


    //uat URL
//    public static String productionURL = "https://uat-arth-seed.whitecoats.com";

    //dev url

    //public static String productionURL = "https://dev-arth-seed.whitecoats.com";


    //stage url
//      public static String productionURL ="https://stage-arth-seed.whitecoats.com";

    //qakb
//          public static String productionURL ="https://qakb-arth-seed.whitecoats.com";


    // Auth URLS
    public static String login = productionURL + "/api/v2/auth/token";

    //Dashboard URLS
    public static String getDashboardStatus = productionURL + "/api/v2/dashboard/get-dashboard-status";
    public static String geDashBoardAppointmentDetails = productionURL + "/api/v2/dashboard/get-dashboard-appointments";
    public static String getDashboardQuickAction = productionURL + "/api/v2/doctor-acl-menu";
    public static String getDashboardSharedRecordAndFollowUp = productionURL + "/api/v3/dashboard/get-dashboard-summary";
    public static String getDoctorDetails = productionURL + "/api/v2/doctors/get-doctor-details";
    public static String getFileFromUrl = productionURL + "/api/v2/get-presigned-url";
    public static String createMessageDashboard = productionURL + "/api/v1/doctors/communications/send-new-message";
    public static String sendDelayAppointment = productionURL + "/api/v2/appointments/delay-intimation";
    public static String bulkAppointmentCancel = productionURL + "/api/v1/dashboard/dashboard-bulk-cancel-appointments";
    public static String bulkAppointmentComplete = productionURL + "/api/v1/dashboard/dashboard-bulk-complete-appointments";
    public static String getFollowUpDefaultSetting = productionURL + "/api/v2/appointments/get-default-follow-up-settings";
    public static String getDashboardQuickLinks = productionURL + "/api/v2/doctor-acl-menu";
    public static String getDashboardBookedTraining = productionURL + "/api/v1/doctors/training-schedules/get-your-booked-training";
    public static String getDashboardUpcomingTraining = productionURL + "/api/v1/doctors/training-schedules/get-traning-schedule";
    public static String cancelTraining = productionURL + "/api/v1/doctors/training-schedules/cancel-booked-training";
    public static String getDashBoardDetails = productionURL + "/api/v2/doctor-acl-menu";

    //Adding subscription plan api
    public static String getDashBoardDetailsSubscriptionPlan = productionURL + "/api/v1/get-doctor-menus-with-plan-details?mode=android";


    public static String sendPaymentReminder = productionURL + "/api/v1/doctors/appointment/send-appointment-payment-reminder";

    //Interface Details URL
    public static String getDoctorInterface = productionURL + "/api/v1/doctors/get-doctor-interfaces";

    //Patient URL
    public static String getPatientDetail = productionURL + "/api/v2/patients/get-patient-details";
    public static String savePatient = productionURL + "/api/v2/patients/add-patient";
    public static String searchPatient = productionURL + "/api/v1/doctors/auto-complete-patient-search";

    //Video Call Api
    public static String getVideoData = productionURL + "/api/v2/appointments/join-video";
    public static String exitVideo = productionURL + "/api/v2/appointments/exit-video";

    //Appointment Api
    public static String getApptStatus = productionURL + "/api/get-appointment-status";
    public static String appointmentDetails = productionURL + "/api/v2/appointments/get-list";
    public static String delayAppointment = productionURL + "/api/v2/appointments/delay-intimation";
    public static String cancelAppt = productionURL + "/api/v2/appointments/cancel-ids";
    public static String serviceDetails = productionURL + "/api/v2/appointments/get-count-by-modes";
    public static String searchMedication = productionURL + "/api/v1/records/get-medicine-list";
    public static String addMedication = productionURL + "/api/v1/records/add-medicine";
    public static String getRecordStructure = productionURL + "/api/v2/records/get-record-structure";
    public static String storeMedication = productionURL + "/api/v1/records/save-multiple-new-record-v2";
    public static String getProcedure = productionURL + "/api/v1/appointment-service/get-service";
    public static String addProcedure = productionURL + "/api/v1/appointment-service/update-appointment-services";
    public static String updateMedicineStatus = productionURL + "/api/v1/records/update-medication-status";

    public static String cancelApptCon = productionURL + "/api/v1/doctors/appointment/cancel-v2-db";


    //EMR Patient Tab URL
    public static String getPatientDetails = productionURL + "/api/v1/doctors/consultation-personal-details-v2";
    public static String updatePatientDetails = productionURL + "/api/v1/doctors/update-personal-details-v2";
    public static String getFamilyData = productionURL + "/api/v1/doctors/consultation-family-list-v2";
    public static String addFamilyData = productionURL + "/api/v1/doctors/consultation-note-add-family-member";

    //EMR ConsultationNotes Tab URL
    public static String checkEncounterForAppointment = productionURL + "/api/v1/records/check-encounter-for-appointment";
    public static String checkEpisodesForAppointment = productionURL + "/api/v1/records/check-episodes-for-patient";
    public static String getEpisodicFieldPreference = productionURL + "/api/v1/records/get-episodic-field-preferences";
    public static String getDoctorEpisode = productionURL + "/api/v1/records/get-doctor-episodes";
    public static String getEncounterDropDown = productionURL + "/api/v1/records/get-dropdown-encounters";
    public static String getAllComponentForCaseHistory = productionURL + "/api/v1/records/get-all-components-records-for-case-history";
    public static String getAllComponentForCaseHistoryEncounter = productionURL + "/api/v1/records/get-all-components-records-for-case-history-encounter";


    public static String getTreatmentPlanDoctorCategory = productionURL + "/api/v1/records/get-doctor-treatmentplan-categories";
    public static String getEvaluationDoctorCategory = productionURL + "/api/v1/records/get-doctor-observation-categories";

    public static String saveNewEpisode = productionURL + "/api/v1/records/save-new-episode";
    public static String saveNewEncounter = productionURL + "/api/v1/records/save-new-encounter";

    public static String getEvaluationFieldPreferences = productionURL + "/api/v1/records/get-evaluation-field-preferences";
    public static String saveSymptomRecords = productionURL + "/api/v2/records/save-new-symptom";
    public static String saveDiagnosisRecords = productionURL + "/api/v1/records/save-new-diagnosis";
    public static String saveInvestigationsRecords = productionURL + "/api/v1/records/save-new-investigation-result";
    public static String getDocDiagnosisDetails = productionURL + "/api/v1/records/get-doctor-diagnoses";
    public static String createPrescriptionRecords = productionURL + "/api/v1/records/create-pdf";
    public static String getPresignedUrl = productionURL + "/api/v2/get-presigned-url";
    public static String sharePrescriptionRecords = productionURL + "/api/v1/records/share-encounter-component-selected-data";
    public static String getAddedNotes = productionURL + "/api/v1/records/get-all-components-records-for-encounter";
    public static String getPrescriptionNoteCount = productionURL + "/api/v1/records/get-prescription-notes-count";


    public static String getArticleImage = productionURL + "/api/v2/get-presigned-url";
    public static String saveNewUploadHandWrittenNotes = productionURL + "/api/v1/records/save-new-uploaded-written-note";
    public static String getPrescriptionDetails = productionURL + "/api/v1/records/get-pdf-history-encounter";

    //EMR shared Records URL
    public static String getEMRSharedRecords = productionURL + "/api/v1/records/get-shared-patient-records-for-timeline";

    //Feeds URL
    public static String getFeedsList = productionURL + "/api/v1/contentlibrary/get-client-content";

    //Utilities URL
    public static String getPresignedURL = productionURL + "/api/v1/get-presigned-url";
    //Create merchant
    public static String createMerchantURL = productionURL + "/api/v1/payu-merchant-v2/create-payu-merchant";
    public static String getCatFieldDetails = productionURL + "/api/v1/payu-merchant-v2/get-create-merchant-field-data";
    public static String getPaymentMerchant = productionURL + "/api/v1/payu-merchant-v2/get-payment-merchant";
    //get
    public static String sendOTP = productionURL + "/api/v1/payu-merchant-v2/send-otp?scope=pennyVerify";
    //post
    public static String pennyVerification = productionURL + "/api/v1/payu-merchant-v2/penny-verification";
    //get
    public static String sendOTPBankUpdate = productionURL + "/api/v1/payu-merchant-v2/send-otp?scope=bankUpdate";
    //post
    public static String connectExistingAccount = productionURL + "/api/v1/payu-merchant-v2/connect-payu-merchant";
    //get dashboard data
    public static String getDashboardData = productionURL + "/api/v1/payu-merchant-v2/get-dashboard-data";
    //update bank details
    public static String postBankDetails = productionURL + "/api/v1/payu-merchant-v2/bank-update";
    //pan details
    public static String postPanDetails = productionURL + "/api/v1/payu-merchant-v2/update-profile";
    //get pan otp
    public static String getPanOTP = productionURL + "/api/v1/payu-merchant-v2/send-otp?scope=profileUpdate";
    //get setUp status
    public static String setUpStatus = productionURL + "/api/v1/payu-merchant-v2/services-list-check";
    //getVerifyAttempts
    public static String getVerifyAttempts = productionURL + "/api/v1/payu-merchant-v2/send-verification-attempts-exhausted-mail";

    public static String doctorSessionInOut = productionURL + "/api/v1/doctors/auth/save-doctor-session";
    public static String authMe = productionURL + "/api/v1/auth/me";
    public static String saveNotificationSetting = productionURL + "/api/v1/doctors/update-doctor-notification-preferences";

    //getAppointmentDetails URL
    public static String getCaptureNotesRecordsList = productionURL + "/api/v1/doctors/appointment/get-captured-notes-data";
    public static String saveNewCaptureAppointmentNotes = productionURL + "/api/v1/doctors/appointment/save-new-appointment-note";
    public static String updateCaptureAppointmentNotes = productionURL + "/api/v1/doctors/appointment/update-appointment-note";
    public static String deleteCaptureAppointmentNotes = productionURL + "/api/v1/doctors/appointment/delete-appointment-notes";
    public static String saveCheckedInStatus = productionURL + "/api/v1/doctors/appointment/save-appointment-check-in-status";
    public static String getAppointmentServicesData = productionURL + "/api/v1/appointment-service/get-services-for-appointment";
    public static String getOrderDetailsData = productionURL + "/api/v1/simplified-payments/get-order-details";
    public static String saveWrittenNotesNewAppt = productionURL + "/api/v1/records/save-new-uploaded-written-note-new-appt";
    public static String checkInStatus = productionURL + "/api/v1/doctors/appointment/get-appointment-check-in-status";
    public static String createInvoice = productionURL + "/api/v1/appointment-service/create-invoice";
    public static String sendFollowUp = productionURL + "/api/v1/follow-up/update-follow-up-for-appt";
    public static String getProductTaxes = productionURL + "/api/v1/doctors/tax/get-product-taxes";

    public static String getPaymentTimeLineData = productionURL + "/api/v1/payment/get-payment-timeline";
    public static String getInvoiceDetailsData = productionURL + "/api/v1/simplified-payments/get-invoice-details";


    //session tracking new api
    public static String doctorSessionSaveLogin = productionURL + "/api/v1/save-login-session";
    public static String doctorSessionSaveStart = productionURL + "/api/v1/save-session-start";
    public static String doctorSessionSaveEnd = productionURL + "/api/v1/save-session-end";
    public static String doctorSessionLogout = productionURL + "/api/v1/save-logout-session";
    public static String doctorSaveSessionActivity = productionURL + "/api/v1/save-session-activity";


    public static String getParsingSimboData = productionURL + "/api/v1/records/get-parsed-simbo-response";

    public static String saveMultipleNewSymptom = productionURL + "/api/v1/records/save-multiple-new-symptoms";
    public static String saveMultipleNewDiagnosis = productionURL + "/api/v1/records/save-multiple-new-diagnosis";
    public static String saveMultipleNewInvestigationResult = productionURL + "/api/v1/records/save-multiple-new-investigation-results";
    public static String saveMultipleNewObservationAndTreatmentPlanResult = productionURL + "/api/v1/records/save-multiple-new-record-v2";

    public static String getSimboAuthKey = productionURL + "/api/v1/records/get-simbo-auth-keys";
    public static String getVoiceEMRPermission = productionURL + "/api/v1/records/get-voice-emr-permission";

    //User Payment Summary Notifications Preference(my payments)
    public static String get_payment_notification_preferences = productionURL + "/api/v2/doctors/get-payment-summary-notifications-preference";
    public static String payment_notification_preferences = productionURL + "/api/v2/doctors/update-payment-summary-notifications-preference";
    public static String payment_transaction_getList = productionURL + "/api/v1/simplified-payments/get-list";
    public static String getPaymentOverviewDetails = productionURL + "/api/v1/simplified-payments/get-overview";
    public static String cancelAppointmentRefund = productionURL + "/api/v1/doctors/refund-order-amount";
    public static String getRefundAmount = productionURL + "/api/v1/simplified-payments/get-refund-amount";

    public static String setUpVideoLink = productionURL + "/api/v1/external-video/update-join-url";

    public static String getVideoToolSetupHelp = productionURL + "/api/v1/external-video/get_video_tool_setup_help";
    public static String saveVideoToolPreference = productionURL + "/api/v1/external-video/set-video-tool-settings";
    public static String notifyPatientExternalJoinVideo = productionURL + "/api/v1/external-video/notify-patient-external-join-video";
    public static String updateTimeFormatPreference = productionURL + "/api/v1/time-pref/update-pref";
    public static String getTimePreferencesData = productionURL + "/api/v1/time-pref/get-pref";


    public static String getGBPLink = productionURL + "/api/v1/gbp-links/get-links";
    public static String SaveGBPLink = productionURL + "/api/v1/gbp-links/update-gbp-link";
    public static String updateGBPSharePreferences = productionURL + "/api/v1/gbp-links/update-share_preference";
    public static String applyToAllGBPLink = productionURL + "/api/v1/gbp-links/apply-link-to-all";
    public static String resetGBPLink = productionURL + "/api/v1/gbp-links/reset-links";

    public static String getStaffPermissionAPI = productionURL + "/api/v1/gbp-links/get-client-gbp-permissions";
    public static String setStaffPermissionAPI = productionURL + "/api/v1/gbp-links/update-client-gbp-permissions";


    public static String getDocDetails_V2 = productionURL + "/api/v2/doctors/get-details";

    //Get All Appointments Dates
    public static String getAllAppointDates = productionURL + "/api/v2/appointments/get-dates";
    //config variables
    public static String appOrigin = "8";

    public static String getIndividualServiceDetails = "/api/v1/doctors/get-doctor-product-details";

    /*Get All Doctor Services Details Used in Setting Page*/
    public static String getAllDoctorServices = productionURL + "/api/v1/doctors/get-doctor-services";


    public static String orderPaymentAmount = productionURL + "/api/v1/appointment-service/get-order-payment-amount";
    public static String getDoctorSettings = productionURL + "/api/v2/doctors/get-doctor-settings";
    public static String checkSendPaymentStatus = productionURL + "/api/v1/check-send-payment-gateway-status";
    public static String cancelRefund = productionURL + "/api/v1/online-payments/cancel-automatic-refund";
    //    public static String cancelAppointmentRefund = productionURL + "/api/v1/doctors/refund-order-amount";
    public static String apptReschedule = productionURL + "/api/v1/appointments/reschedule";

    public static String getBookedTraining = productionURL + "/api/v1/doctors/training-schedules/get-your-booked-training";
    //    public static String cancelTraining = productionURL + "/api/v1/doctors/training-schedules/cancel-booked-training";
    public static String bookTraining = productionURL + "/api/v1/doctors/training-schedules/book-traning-schedule";
    public static String bookedTrainingReschedule = productionURL + "/api/v1/doctors/training-schedules/reschedule-booked-training";
    public static String logoutApp = productionURL + "/api/v1/log";

    public static String postNewDiscussion = productionURL + "/api/v1/doctors/case-send-chat-message";
    public static String caseDiscussionSummary = productionURL + "/api/v1/doctors/get-case-discussions-summary";
    public static String getDoctorMerchantStatus = productionURL + "/api/v1/payu-merchant/check-doctor-merchant-status";
    public static String createMerchant = productionURL + "/api/v1/payu-merchant/create-merchant";
    public static String getfinancialMerchantDetails = productionURL + "/api/v1/payu-merchant/get-merchant";
    public static String getUpcomingTraining = productionURL + "/api/v1/doctors/training-schedules/get-traning-schedule";

    //case channel
    public static String getCaseDiscussion = productionURL + "/api/v1/doctors/get-case-discussions?";
    public static String getDoctorOrganisation = productionURL + "/api/v1/doctors/get-mapping-for-create-discussion";
    public static String updateDoctorOrganisationDiscussion = productionURL + "/api/v1/doctors/update-discussion-mapping";
    public static String createNewCaseChannel = productionURL + "/api/v1/doctors/save-case-discussion";
    public static String getChannelDiscussions = productionURL + "/api/v1/doctors/get-case-chat-messages";
    public static String getCaseTasks = productionURL + "/api/v1/doctors/get-case-tasks";
    public static String getCaseTaskAssignedFilter = productionURL + "/api/v1/doctors/get-case-details";
    public static String saveCaseTask = productionURL + "/api/v1/doctors/save-case-task";
    public static String getGetCaseDiscussionEncounter = productionURL + "/api/v1/records/get-dropdown-encounters-for-case-discussion";
    public static String getCaseDiscussionWritten_Notes = productionURL + "/api/v1/records/get-records-for-written-notes-episode-case-discussion";
    public static String getCaseDiscussionEvaluation = productionURL + "/api/v1/records/get-records-for-evaluation-episode-case-discussion";
    public static String getCaseDiscussionObservations = productionURL + "/api/v1/records/get-records-for-observations-episode-case-discussion";
    public static String getCaseDiscussionTreatmentplan = productionURL + "/api/v1/records/get-records-for-treatmentplan-episode-case-discussion";
    public static String getCaseChannelDashBoardSummaryDetails = productionURL + "/api/v1/doctors/get-case-details";
    public static String getCaseChannelDashBoardSummary = productionURL + "/api/v1/doctors/get-dashboard-data";
    public static String updateTaskStatus = productionURL + "/api/v1/doctors/update-case-task";

    public static String aiQueryLink = "https://console.dialogflow.com/api/query?v=20170712";
    public static String aiRouter = productionURL + "/api/ai/v2/ai-router";
    public static String aiWelcomeText = productionURL + "/api/v1/get-ai-welcome-text-doctor";
    //    public static String updatePatientDetails = productionURL + "/api/v2/patients/update-personal-details";
    public static String getDoctorsDetails = productionURL + "/api/v2/doctors/get-doctor-details";
    public static String getDoctorsSlot = productionURL + "/api/v2/appointments/get-available-slots";
    public static String consultQuickOption = productionURL + "/api/v1/doctors/consultation-quick-options";
    public static String saveFamilyData = productionURL + "/api/v1/doctors/consultation-note-add-family-member";
    public static String getSharedRecordsCount = productionURL + "/api/v1/doctors/get-record-count";
    public static String getSharedRecordDetails = productionURL + "/api/v1/doctors/get-records-details";
    public static String getEpisEncounter = productionURL + "/api/v1/records/get-dropdown-encounters";
    public static String getEpisPdf = productionURL + "/api/v1/records/get-pdf-history-episode";
    public static String getEpisEvaluation = productionURL + "/api/v1/records/get-records-for-evaluation-episode";
    public static String getEpisTreatmentPlan = productionURL + "/api/v1/records/get-records-for-treatmentplan-episode";
    public static String getEcntPdf = productionURL + "/api/v1/records/get-pdf-history-encounter";
    public static String getEcntWrittenNotes = productionURL + "/api/v2/records/get-records-for-written-notes-encounter";
    public static String getEcntEvaluation = productionURL + "/api/v1/records/get-records-for-evaluation-encounter";
    public static String getEcntTreatmentPlan = productionURL + "/api/v1/records/get-records-for-treatmentplan-encounter";
    public static String bookAppointment = productionURL + "/api/v2/appointments/book-appt";
    //    public static String delayAppointment = productionURL + "/api/v2/appointments/delay-intimation";
    public static String signUpUser = productionURL + "/api/v2/auth/register";
    public static String saveSymptom = productionURL + "/api/v2/records/save-new-symptom";
    public static String saveInvestData = productionURL + "/api/v2/records/save-new-investigation-result";
    //    public static String getRecordStructure = productionURL + "/api/v2/records/get-record-structure";
    public static String saveRecords = productionURL + "/api/v2/records/save-new-record-v2";
    public static String updateDynamicRecords = productionURL + "/api/v1/records/update-dynamic-record";
    public static String getActiveAndPastChatList = productionURL + "/api/v2/chats/chats-list";
    public static String getOngoingChatList = productionURL + "/api/v2/chats/get-ongoing-chat-messages";
    public static String saveChatMessage = productionURL + "/api/v2/chats/save-chat-message";
    public static String shareEpisodeData = productionURL + "/api/v2/records/share-encounter-component-selected-data";
    public static String getOrganisationLogo = productionURL + "/api/v1/doctors/get-organisation-logo";
    public static String getPrivacyPolicy = productionURL + "/api/v2/get-privacy-policy-and-terms";
    public static String setupVideoServiceSetting = productionURL + "/api/v1/doctors/service-setup";
    public static String insertNotiPlayerId = productionURL + "/api/v2/notification/insert-player-ids";
    public static String createReceipt = productionURL + "/api/v1/doctors/create-order-receipt";
    public static String getDocDiagnoses = productionURL + "/api/v1/records/get-doctor-diagnoses";
    public static String saveDiagnoses = productionURL + "/api/v1/records/save-new-diagnosis";
    public static String getOrderDetails = productionURL + "/api/v1/doctors/orders/get-order-details";
    public static String getSharedRecordPatientList = productionURL + "/api/v2/records/get-shared-records-patient-list";

    public static String getPatientSharedRecordsCount = productionURL + "/api/v2/records/get-shared-categories-for-patient-on-date";
    public static String getPatientSharedRecordDetails = productionURL + "/api/v2/records/get-shared-records-for-patient-on-date";
    //    public static String getFollowUpDefaultSetting = productionURL + "/api/v2/appointments/get-default-follow-up-settings";
    public static String bulkAppointmentCompleteTwo = productionURL + "/api/v2/appointments/bulk-appt-complete";
    public static String getFollowUpPref = productionURL + "/api/v1/follow-up/get-doctor-followup-prefrences";
    public static String updateFollowUpPref = productionURL + "/api/v1/follow-up/update-doctor-followup-prefrences";

    //    public static String getEvaluationFieldPreferences = productionURL + "/api/v1/records/get-evaluation-field-preferences";
    public static String generateRecordPdf = productionURL + "/api/v1/records/create-pdf";
    public static String getPreSignedUrl = productionURL + "/api/v1/get-presigned-url";
    public static String updateFollowUpSubmissionRow = productionURL + "/api/v1/follow-up/update-followup-submission-row";

    public static String getFollowUpList = productionURL + "/api/v2/follow-up/get-follow-up-list";


    public static String getVideoToolSettingDetails = productionURL + "/api/v1/external-video/get-video-tool-settings";
    public static String updateSettingService = productionURL + "/api/v1/doctors/update-service-setting";
    public static String updateDoctorServiceSetting = productionURL + "/api/v1/doctors/update-doctor-service-setting";
    public static String updateServicePermission = productionURL + "/api/v1/doctors/update-service-permission";
    public static String updateInstantVideoServicePermission = productionURL + "/api/v1/doctors/update-instant-permission";
    public static String updateDoctorPermission = productionURL + "/api/v1/doctors/update-doctor-permission";
    public static String getEpisodicFieldPref = productionURL + "/api/v1/records/get-episodic-field-preferences";
    public static String saveEpisodicFieldPref = productionURL + "/api/v1/records/save-episodic-control-preferences";
    public static String getEvaluationControlPref = productionURL + "/api/v1/records/get-evaluation-control-preferences";
    public static String saveEvaluationControlPref = productionURL + "/api/v1/records/save-evaluation-control-preferences";
    public static String getAllObservationCategory = productionURL + "/api/v1/records/get-all-observation-categories";
    public static String getDoctorObservationCategory = productionURL + "/api/v1/records/get-doctor-observation-categories";
    public static String saveObservationCategory = productionURL + "/api/v1/records/save-episodic-non-episodic-categories";
    public static String getAllTreatmentCategory = productionURL + "/api/v1/records/get-all-treatmentplan-categories";
    public static String getDoctorTreatmentCategory = productionURL + "/api/v1/records/get-doctor-treatmentplan-categories";
    public static String getDoctorTimeBlock = productionURL + "/api/v1/doctors/get-doctor-time-block";
    public static String deleteDoctorTimeBlock = productionURL + "/api/v1/doctors/delete-doctor-time-block";
    public static String changeDoctorTimeBlockStatus = productionURL + "/api/v1/doctors/change-time-block-status";
    public static String doctorServiceTimeBlock = productionURL + "/api/v1/doctors/get-doctor-services-for-time-block";
    public static String saveDoctorServiceTimeBlock = productionURL + "/api/v1/doctors/save-doctor-time-block";
    //    public static String getDashBoardDetails = productionURL + "/api/v2/doctor-acl-menu";
    public static String getDashBoardSummaryDetails = productionURL + "/api/v2/dashboard-summary";
    public static String getCommunicationCount = productionURL + "/api/v2/communications/get-count";
    public static String getAppointmentList = productionURL + "/api/v2/appointments/get-list";
    public static String updateAppointmentType = productionURL + "/api/v1/doctors/appointment/update-appointment-type";
    public static String getEpisodicNonEpisodicPref = productionURL + "/api/v2/records/get-episodic-nonepisodic-preferences";
    public static String getFamilyList = productionURL + "/api/v2/patients/consultation-family-list-v2";
    public static String getEpisodicCatWithRecord = productionURL + "/api/v2/records/get-nonepisodic-categories-with-records";
    //    public static String getPatientDetailsTwo = productionURL + "/api/v2/patients/get-patient-details";
    public static String getPatientBackground = productionURL + "/api/v2/patients/consultation-personal-details-v2";
    //    public static String saveNewEpisode = productionURL + "/api/v2/records/save-new-episode";
    public static String getEpisodes = productionURL + "/api/v2/records/get-doctor-episodes";
    public static String saveNewInteraction = productionURL + "/api/v2/records/save-new-encounter";
    public static String checkApptInteraction = productionURL + "/api/v2/records/check-encounter-for-appointment";
    public static String checkPatientEpisode = productionURL + "/api/v2/records/check-episodes-for-patient";
    public static String getWrittenNotes = productionURL + "/api/v2/records/get-records-for-written-notes-episode";
    //    public static String getFileFromUrl = productionURL + "/api/v2/get-presigned-url";
    public static String uploadRecordImage = productionURL + "/api/v1/doctors/image-upload";
    public static String saveWrittenNotes = productionURL + "/api/v2/records/save-new-uploaded-written-note";
    public static String updatePaymentStatus = productionURL + "/api/v2/appointments/update-service-order";
    public static String getSavedCategory = productionURL + "/api/v1/doctors/get-saved-categories";
    public static String getDoctorCategory = productionURL + "/api/v1/doctors/get-doctor-categories";
    public static String savePatientCategory = productionURL + "/api/v1/doctors/save-patient-category-tag";
    public static String getPatientList = productionURL + "/api/v2/patients/list";
    public static String getEncounterDetailsForAppt = productionURL + "/api/v2/records/get-encounter-details-for-appointment";
    public static String saveApptAttendanceStatus = productionURL + "/api/v2/appointments/save-appointment-check-clinicplus-status";
    public static String getApptAttendanceStatus = productionURL + "/api/v2/appointments/get-appointment-check-clinicplus-status";
    public static String cancelApptTwo = productionURL + "/api/v1/doctors/appointment/cancel-v2-db";
    public static String updateClinicAppt = productionURL + "/api/v1/doctors/appointment/update-clinic-status";
    public static String getDoctorDetail = productionURL + "/api/v2/doctors/get-doctor-details";
    public static String updatePersionalDetails = productionURL + "/api/v2/doctors/update-doctor-profile";
    public static String getYouTubeInfo = "https://www.googleapis.com/youtube/v3/videos";
    public static String getDoctorInterFace = productionURL + "/api/v1/doctors/get-doctor-interfaces";
    public static String addPatientDetails = productionURL + "/api/v2/patients/add-patient";
    public static String cancelAllAppts = productionURL + "/api/v2/appointments/cancel-ids";
    public static String createMessage = productionURL + "/api/v1/doctors/communications/send-new-message";
    public static String textArticleUploadImage = productionURL + "/api/v1/doctors/image-upload";
    public static String getTextArticlesDetails = productionURL + "/api/v1/contentlibrary/get-text-article-details";
    public static String saveTextArticlesDetails = productionURL + "/api/v1/contentlibrary/save-article";
    public static String saveArticlesDetailsExternal = productionURL + "/api/v1/contentlibrary/save-article-external";
    public static String getCommunicationArticlesDetails = productionURL + "/api/v2/communications/articles/get-articles-video";
    public static String savePdfArticles = productionURL + "/api/v2/communications/articles/save-file-article";
    public static String getCommunicationArticlesText = productionURL + "/api/v2/communications/articles/get-articles-text";
    public static String getArticleImageTwo = productionURL + "/api/v1/get-presigned-url-open";
    public static String getCommunicationPdfArticles = productionURL + "/api/v2/communications/articles/get-articles-pdf";
    public static String deleteContentArticles = productionURL + "/api/v1/contentlibrary/delete-content-value/";
    public static String getPastMessage = productionURL + "/api/v1/doctors/communications/get-past-messages";
    public static String getProfesionalLanguage = productionURL + "/api/v1/doctors/get-languages";
    public static String updateProfesional = productionURL + "/api/v2/doctors/update-doctor-professional-details";
    public static String getJoinVideoDetails = productionURL + "/api/v2/appointments/join-video";
    //    public static String exitVideo = productionURL + "/api/v2/appointments/exit-video";
    public static String getWrittenNoteForEncounter = productionURL + "/api/v2/records/get-records-for-written-notes-encounter";


    public static String loginUrl = productionURL + "/api/v2/auth/token";
    public static String forgotPassOTP = productionURL + "/api/v2/auth/forgot-password";
    public static String forgotPassUpdate = productionURL + "/api/v2/auth/update-password";
    public static String logout = productionURL + "/api/v2/auth/logout-doctor";
    //public String authMe = productionURL + "/api/v1/auth/me";
    public static String getDoctorDetailsTwo = productionURL + "/api/v1/doctors/get-details";
    public static String getDoctorDetailsProfile = productionURL + "/api/v2/doctors/doctor-profile";
    public static String getfinancial = productionURL + "/api/v1/doctors/get-bank-details";
    public static String saveFinacialDetails = productionURL + "/api/v1/doctors/save-bank-details";
    public static String getAppReminder = productionURL + "/api/v1/doctors/get-clinic-reminders";
    public static String getAppReschedulePreferences = productionURL + "/api/v1/settings/get-notifications-preference";
    public static String updateRescheduleNotificationPreferences = productionURL + "/api/v1/settings/update-notifications-preference";
    public static String saveAppReminder = productionURL + "/api/v1/doctors/update-clinic-reminders";
    public static String getAppStatics = productionURL + "/api/v1/doctors/appointment/get-appointment-average-time";
    public static String saveAppStatics = productionURL + "/api/v1/doctors/appointment/update-appointment-average-preferences";
    public static String getSetting = productionURL + "/api/v1/doctors/get-settings";

    public static String getAutoSuggestionsForSymptoms = productionURL + "/api/v1/records/get-symptom-suggestion-list";
    public static String getAutoSuggestionsForMedicationMedicienName = productionURL + "/api/v1/records/get-medicine-list";
    public static String updatePrescriptionRecord = productionURL + "/api/v1/records/update-prescription-records";
    public static String deletePrescriptionRecord = productionURL + "/api/v1/records/delete-prescription-records";

    //Organisation Doctor's list
    public static String getOrganisaitionDocList = productionURL + "/api/v1/records/get-organisation-doctors";

    /*Payment option details API*/
    public static String getPaymentOptionsDetails = productionURL + "/api/v1/payment/options";
    public static String savePaymentOptions = productionURL + "/api/v1/payment/options";
    public static String setDefaultPaymentMode = productionURL + "/api/v1/payment/update-default-payment-mode";
    public static String checkMerchantStatus = productionURL + "/api/v1/payment/get-merchant-status";
    public static String getSingleDoctorInterface = productionURL + "/api/v1/doctors/get-single-doctor-interface";


    //shared pref variables
    public static String appSharedPref = "OHPFA";
    public String loginSessionPref = "LoginSession";
    public String docName = "DocName";
    public static String docId = "DocId";
    public String isSpecList = "0";
    public String isDocList = "0";

    public static String isLoginStatus="isLoginStatus";
    public static String loginToken;
    public static int doctorId;
    public static int login_counter;
    public static String lastLoginDate = "lastLoginDate";
    public static int patientId;
    public static String chatId;
    public static String playerId = "PlayerID";
    public String signUpName = "SignUpName";
    public String signUpEmail = "SignUpEmail";
    public String signUpPhone = "SignUpPhone";
    public String signUpOTPStr = "SignUpOTP";
    public String signInToken = "SignInToken";
    public static int isDoctorOnly;
    public static int homeGuideTab = 0;
    public static int spinnerSelection = 2;
    public static String authKey = "";
    public static String wss1_url = "";
    public static String devId = "";

    //bottom navigation status
    public static int bottomNaviType = 0;
    public static String activePastFilterFlag = "";
    public static int videoServiceStatus = -1;
    public static int clinicServiceStatus = -1;
    public static int chatServiceStatus = -1;


    //dialog flow ai api token
    public static String aiClientId = "c83e19a2e4f34f1fb17dd7b90e842e0c"; //client id //old id eb5059a850be4700b837af39c4d88014
//    public String aiClientId = "eb5059a850be4700b837af39c4d88014"; //client id //old id


    public static Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("App-Origin", ApiUrls.appOrigin);
        headers.put("Authorization", "Bearer " + ApiUrls.loginToken);
        return headers;
    }
}
